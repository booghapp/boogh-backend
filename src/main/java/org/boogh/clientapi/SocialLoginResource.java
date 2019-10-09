package org.boogh.clientapi;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.undertow.util.BadRequestException;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.boogh.config.ApplicationProperties;
import org.boogh.config.Constants;
import org.boogh.domain.Memcache;
import org.boogh.domain.TelegramChat;
import org.boogh.domain.User;
import org.boogh.repository.MemcacheRepository;
import org.boogh.repository.TelegramChatRepository;
import org.boogh.repository.UserRepository;
import org.boogh.security.jwt.JWTFilter;
import org.boogh.security.jwt.TokenProvider;
import org.boogh.service.UserService;
import org.boogh.service.dto.UserDTO;
import org.boogh.web.rest.UserJWTController;
import org.boogh.web.rest.errors.BadRequestAlertException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;


@Controller
@RequestMapping(Constants.API_VERSION + "/social")
public class SocialLoginResource {

    private final String ENTITY_NAME = "social login";

    private final Logger log = LoggerFactory.getLogger(SocialLoginResource.class);

    private final UserService userService;

    private final UserRepository userRepository;

    private final TokenProvider tokenProvider;

    private final AuthenticationManager authenticationManager;

    private final CacheManager cacheManager;

    private final ApplicationProperties applicationProperties;

    private final TelegramChatRepository telegramChatRepository;

    private final MemcacheRepository memcacheRepository;

    public SocialLoginResource(UserService userService, UserRepository userRepository, TokenProvider tokenProvider,
                               AuthenticationManager authenticationManager, CacheManager cacheManager, ApplicationProperties applicationProperties,
                               TelegramChatRepository telegramChatRepository, MemcacheRepository memcacheRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.cacheManager = cacheManager;
        this.applicationProperties = applicationProperties;
        this.telegramChatRepository = telegramChatRepository;
        this.memcacheRepository = memcacheRepository;
    }

    /**
     * Send request to twitter api for request token.
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/twitter/request_token", method = RequestMethod.GET)
    public ResponseEntity<String>  getTwitterRequestToken(@RequestParam String redirectUri) throws IOException {

        String authorizationHeader = constructAuthorizationHeader(redirectUri);

        String oauthToken = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.twitter.com/oauth/request_token");

        httpPost.setHeader("Authorization", authorizationHeader);
        CloseableHttpResponse httpResponse = httpclient.execute(httpPost);

        try {
            HttpEntity entity = httpResponse.getEntity();
            String body = EntityUtils.toString(entity);
            String tokenParam = "oauth_token=";
            String secretParam = "&oauth_token_secret=";
            oauthToken = body.substring(body.indexOf(tokenParam) + tokenParam.length(), body.indexOf(secretParam));
            EntityUtils.consume(entity);

        } catch (ClientProtocolException cpe) {
            System.out.println(cpe.getMessage());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        } finally {
            httpResponse.close();
        }

        return ResponseEntity.ok().body(oauthToken);
    }

    /**
     * Verify telegram user.
     * @param id
     * @param first_name
     * @param last_name
     * @param username
     * @param photo_url
     * @param auth_date
     * @param hash
     */
    @RequestMapping(value = "/telegram", method = RequestMethod.POST)
    public ResponseEntity<UserJWTController.JWTToken> handleTelegramLogin(
        @RequestParam Long id,
        @RequestParam String first_name,
        @RequestParam String last_name,
        @RequestParam String username,
        @RequestParam String photo_url,
        @RequestParam Long auth_date,
        @RequestParam String hash) throws NoSuchAlgorithmException, InvalidKeyException{

        //Construct Data-check-string
        String dataCheckString = "auth_date=" + auth_date;
        if (!first_name.equals("")) {
            dataCheckString += "\nfirst_name=" + first_name;
        }
        dataCheckString += "\nid=" + id;
        if (!last_name.equals("")) {
            dataCheckString += "\nlast_name=" + last_name;
        }
        if (!photo_url.equals("")) {
            dataCheckString += "\nphoto_url=" + photo_url;
        }
        if (!username.equals("")) {
            dataCheckString += "\nusername=" + username;
        }

        String telegramToken = applicationProperties.getTelegram().getBotToken();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] secret_key = digest.digest(
            telegramToken.getBytes(StandardCharsets.UTF_8));

        final Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

        SecretKey sKey = new SecretKeySpec(secret_key, "SHA-256");

        sha256_HMAC.init(sKey);
        byte[] encoding = sha256_HMAC.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

        String hexRepresentation = new String(Hex.encode(encoding));

        if (hexRepresentation.equals(hash)) {
            // User is authenticated
            User user = createOrRetrieveSocialUser(first_name, first_name + "@telegram.com");
            // Check if chat_id exist for user, if not do the following:
            String memCache = "";
            if (telegramChatRepository.findTelegramChatByUserId(user.getId()).isEmpty()){
                memCache = generateMemCacheKey(user.getId(), id);
            }
            return createJWTForSocialUser(user, memCache);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value="/telegram", params = {"hash", "chat_id"})
    public ResponseEntity setTelegramChatId(@RequestParam String hash, @RequestParam Long chat_id) {

        Optional<Memcache> memcache = memcacheRepository.findMemcacheByHash(hash);
        if (!memcache.isPresent()) {
            throw new BadRequestAlertException("Incorrect hash", ENTITY_NAME, "Incorrect hash");
        }
        TelegramChat telegramChat = new TelegramChat();
        telegramChat.setTelegramUserId(memcache.get().getTelegramId());
        telegramChat.setUser(memcache.get().getUser());
        telegramChat.setChatId(chat_id);
        telegramChatRepository.save(telegramChat);
        memcacheRepository.delete(memcache.get());
        return ResponseEntity.ok().body("");
    }

    @PostMapping(value="/botAuth", params = {"auth", "telegram_user_id"})
    public ResponseEntity<UserJWTController.JWTToken> authenticateBotUser(@RequestParam String auth, @RequestParam Long telegram_user_id) throws NoSuchAlgorithmException, BadRequestException {
        // hash (secret + telegram_user_id)
        // compare with auth, if equals user is authenticated
        // Return JWT
        String secretWithUserId = applicationProperties.getTelegram().getBotSecret() + telegram_user_id;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(
            secretWithUserId.getBytes(StandardCharsets.UTF_8));
        String encodedHash = new String(Hex.encode(hash));
        ResponseEntity<UserJWTController.JWTToken> responseEntity = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        if (encodedHash.equals(auth)) {
            //Bot user authenticated, return JWT
            Optional<TelegramChat> telegramChat = telegramChatRepository.findTelegramChatByTelegramUserId(telegram_user_id);
            if (!telegramChat.isPresent()) {
                throw new BadRequestException();
            }
            Optional<User> optionalUser = userRepository.findUserByTelegramChatEquals(telegramChat.get());
            if (!optionalUser.isPresent()) {
                throw new BadRequestException();
            }
            responseEntity = createJWTForSocialUser(optionalUser.get(), "");
        }
        return responseEntity;
    }

    /**
     * Verify twitter user.
     * @param verifier
     * @param token
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/twitter", method = RequestMethod.POST)
    public ResponseEntity<UserJWTController.JWTToken> handleTwitterLogin(@RequestParam String verifier, @RequestParam String token, @RequestParam String redirectUri) throws IOException {

        String authorizationHeader = constructAuthorizationHeader(redirectUri);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.twitter.com/oauth/access_token?oauth_verifier=" + verifier + "&oauth_token=" + token);
        httpPost.setHeader("Authorization", authorizationHeader);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

        try {
            HttpEntity entity = httpResponse.getEntity();
            String body = EntityUtils.toString(entity);

            if (httpResponse.getStatusLine().getStatusCode() == 200){

                String tokenParam = "oauth_token=";
                int oauthIndex = body.indexOf(tokenParam);

                String secretParam = "&oauth_token_secret=";
                int secretIndex = body.indexOf(secretParam);

                String userIdParam = "&user_id=";
                int userIdIndex = body.indexOf(userIdParam);

                String oauthToken = body.substring(oauthIndex + tokenParam.length(), secretIndex);

                String secret = body.substring(secretIndex + secretParam.length(), userIdIndex);

                final String twitterApiKey = applicationProperties.getTwitter().getApiKey();
                final String twitterApiSecret = applicationProperties.getTwitter().getApiSecret();

                Twitter twitter = new TwitterTemplate(twitterApiKey, twitterApiSecret, oauthToken, secret);
                TwitterProfile profile = twitter.userOperations().getUserProfile();
                String login = twitter.userOperations().getScreenName();

                // Get the twitter users email
                RestTemplate restTemplate = new TwitterTemplate(twitterApiKey, twitterApiSecret, oauthToken, secret).getRestTemplate();
                String response = restTemplate.getForObject("https://api.twitter.com/1.1/account/verify_credentials.json?include_email=true", String.class);
                JSONObject json = new JSONObject(response);
                String email = json.getString("email");

                // Check if the twitter user exists
                User user = createOrRetrieveSocialUser(login, email);
                return createJWTForSocialUser(user, "");
            }

            EntityUtils.consume(entity);

        } catch (ClientProtocolException cpe) {
            System.out.println(cpe.getMessage());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            httpResponse.close();
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * Verify google user.
     * @param code
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    @RequestMapping(value = "/google", method = RequestMethod.POST)
    public ResponseEntity<UserJWTController.JWTToken> handleGoogleLogin(@RequestParam String code, @RequestParam String redirectUri) throws IOException {

        // Exchange auth code for access token
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
        final String googleClientId = applicationProperties.getGoogle().getClientId();
        final String googleClientSecret = applicationProperties.getGoogle().getClientSecret();
        clientSecrets.set("client_id", googleClientId);
        clientSecrets.set("client_secret", googleClientSecret);

        GoogleTokenResponse tokenResponse =
            new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                "https://www.googleapis.com/oauth2/v4/token",
                (String) clientSecrets.get("client_id"),
                (String) clientSecrets.get("client_secret"),
                code,
                redirectUri)  // Specify the same redirect URI that you use with your web
                // app. If you don't have a web version of your app, you can
                // specify an empty string.
                .execute();

        String accessToken = tokenResponse.getAccessToken();

        // Get profile info from ID token
        GoogleIdToken idToken = tokenResponse.parseIdToken();
        GoogleIdToken.Payload payload = idToken.getPayload();
        String userId = payload.getSubject();  // Use this value as a key to identify a user.
        String email = payload.getEmail();
        boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
        String givenName = (String) payload.get("given_name");

        // Check if the google user exists

        User user = createOrRetrieveSocialUser(givenName, email);
        return createJWTForSocialUser(user, "");
    }


    private String constructAuthorizationHeader(String redirectUri) throws UnsupportedEncodingException {
        String oauth_signature_method = "HMAC-SHA1";
        final String twitterApiKey = applicationProperties.getTwitter().getApiKey();
        String oauth_consumer_key = twitterApiKey;
        String uuid_string = UUID.randomUUID().toString();
        uuid_string = uuid_string.replace("-", "");
        String oauth_nonce = uuid_string;
        String oauth_timestamp = CurrentUNIXTimestamp.Get();
        String oauth_callback = URLEncoder.encode(redirectUri, "UTF-8");

        String parameter_string = "oauth_callback=" + oauth_callback + "&oauth_consumer_key=" + oauth_consumer_key + "&oauth_nonce=" + oauth_nonce + "&oauth_signature_method=" +
            oauth_signature_method + "&oauth_timestamp=" + oauth_timestamp + "&oauth_version=1.0";

        String signature_base_string = "POST&https%3A%2F%2Fapi.twitter.com%2Foauth%2Frequest_token&" + URLEncoder.encode(parameter_string, "UTF-8");

        String oauth_signature = "";
        try {
            final String twitterApiSecret = applicationProperties.getTwitter().getApiSecret();
            oauth_signature = computeSignature(signature_base_string, twitterApiSecret + "&");  // note the & at the end. Normally the user access_token would go here, but we don't know it yet for request_token
            System.out.println("oauth_signature=" + URLEncoder.encode(oauth_signature, "UTF-8"));
        } catch (GeneralSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String authorization_header_string = "OAuth oauth_callback=\"" + oauth_callback +  "\",oauth_consumer_key=\"" + oauth_consumer_key + "\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"" +
            oauth_timestamp + "\",oauth_nonce=\"" + oauth_nonce + "\",oauth_signature=\""
            + URLEncoder.encode(oauth_signature, "UTF-8") + "\",oauth_version=\"1.0\"";

        return authorization_header_string;
    }

    private static String computeSignature(String baseString, String keyString) throws GeneralSecurityException{

        SecretKey secretKey = null;

        byte[] keyBytes = keyString.getBytes();
        secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");

        Mac mac = Mac.getInstance("HmacSHA1");

        mac.init(secretKey);

        byte[] text = baseString.getBytes();

        return new String(Base64.getEncoder().encode(mac.doFinal(text))).trim();
    }

    private static class CurrentUNIXTimestamp {
        public static String Get() {
            Date currentDate = new Date();
            Long currentUnixTime = currentDate.getTime() / 1000L;
            return currentUnixTime.toString();
        }
    }

    /**
     * Register and return JWT for users signing / logging in with social media.
     * @param user
     * @return
     */
    private ResponseEntity<UserJWTController.JWTToken> createJWTForSocialUser(User user, String memCache) {

        // Authenticate the user and return a JWT token
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(user.getLogin(), "");

        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, false);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        httpHeaders.add("userId", String.valueOf(user.getId()));
        httpHeaders.add("telegram-hash", String.valueOf(memCache));
        return new ResponseEntity<>(new UserJWTController.JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    /**
     * Create or retrieve social user.
     * @param login
     * @param email
     * @return
     */
    private User createOrRetrieveSocialUser(String login, String email) {
        Optional<User> user = userRepository.findOneByEmailIgnoreCase(email);
        UserDTO userDTO = new UserDTO();
        userDTO.setLogin(login);
        userDTO.setEmail(email);

        if (user.orElse(null) == null) {
            // Register the user
            Boolean isSocialLogin = true;
            User createdUser = userService.registerUser(userDTO, "", isSocialLogin);
            user = Optional.of(createdUser);
        }
        return user.get();
    }


    @Cacheable(value = "MemCache", key = "#memCacheKey")
    public String generateMemCacheKey(Long userId, Long telegramUserId){

        Optional<Memcache> memcache = memcacheRepository.findMemcacheByTelegramId(telegramUserId);
        if (memcache.isPresent()) {
            return memcache.get().getHash();
        }
        final int KEY_LENGTH = 25;
        char[] characters = "1234567890qwertyuuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM_-".toCharArray();
        String hash = "";
        for (int i = 0; i < KEY_LENGTH; i++) {
            hash += characters[(int) (Math.random() * characters.length)];
        }
        Memcache freshMemCache = new Memcache();
        freshMemCache.setUser(userRepository.findOneWithAuthoritiesById(userId).get());
        freshMemCache.setHash(hash);
        freshMemCache.setTelegramId(telegramUserId);
        memcacheRepository.save(freshMemCache);
        return hash;
    }
}
