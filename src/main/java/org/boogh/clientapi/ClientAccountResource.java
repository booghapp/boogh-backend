package org.boogh.clientapi;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.boogh.config.Constants;
import org.boogh.domain.User;
import org.boogh.security.jwt.JWTFilter;
import org.boogh.security.jwt.TokenProvider;
import org.boogh.service.MailService;
import org.boogh.service.UserService;
import org.boogh.service.dto.PasswordChangeDTO;
import org.boogh.service.dto.UserDTO;
import org.boogh.web.rest.UserJWTController;
import org.boogh.web.rest.errors.*;
import org.boogh.web.rest.vm.KeyAndPasswordVM;
import org.boogh.web.rest.vm.LoginVM;
import org.boogh.web.rest.vm.ManagedUserVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping(Constants.API_VERSION)
public class ClientAccountResource {

    private final Logger log = LoggerFactory.getLogger(ClientAccountResource.class);

    private final UserService userService;

    private final MailService mailService;

    private final AuthenticationManager authenticationManager;

    private final TokenProvider tokenProvider;

    private static final String ENTITY_NAME = "account";


    public ClientAccountResource(UserService userService, MailService mailService, AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.userService = userService;
        this.mailService = mailService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    /**
     * POST  /register : register the user.
     *
     * @param managedUserVM the managed user View Model
     * @throws InvalidPasswordException 400 (Bad Request) if the password is incorrect
     * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already used
     * @throws LoginAlreadyUsedException 400 (Bad Request) if the login is already used
     */
    @PostMapping("/register")
    @Timed
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        Boolean isSocialLogin = false;
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword(), isSocialLogin);
        user.setLangKey("fa");
        mailService.sendActivationEmail(user);
    }

    /**
     * GET  /activate : activate the registered user.
     *
     * @param key the activation key
     * @throws RuntimeException 500 (Internal Server Error) if the user couldn't be activated
     */
    @GetMapping("/activate")
    @Timed
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new InternalServerErrorException("No user was found for this activation key");
        }
    }

    /**
     * GET  /authenticate : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request
     * @return the login if the user is authenticated
     */
    @GetMapping("/authenticate")
    @Timed
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET  /account : get the current user.
     *
     * @return the current user
     * @throws RuntimeException 500 (Internal Server Error) if the user couldn't be returned
     */
    @GetMapping("/account")
    @Timed
    public UserDTO getAccount() {
        return userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new InternalServerErrorException("User could not be found"));
    }

    /**
     * POST  /account : update the current user information.
     *
     * @param userDTO the current user information
     * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already used
     * @throws RuntimeException 500 (Internal Server Error) if the user login wasn't found
     */
    @PostMapping("/account")
    @Timed
    public void saveAccount(@Valid @RequestBody UserDTO userDTO) {
        userService.updateUserBasicInfo(userDTO);
    }

    /**
     * POST  /account/change-password : changes the current user's password
     *
     * @param passwordChangeDto current and new password
     * @throws InvalidPasswordException 400 (Bad Request) if the new password is incorrect
     */
    @PostMapping(path = "/account/change-password")
    @Timed
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (!checkPasswordLength(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * POST   /account/reset-password/init : Send an email to reset the password of the user
     *
     * @param mail the mail of the user
     * @throws EmailNotFoundException 400 (Bad Request) if the email address is not registered
     */
    @PostMapping(path = "/account/reset-password/init")
    @Timed
    public void requestPasswordReset(@RequestBody String mail) {
        int colonIndex = mail.indexOf(':');
        String email = mail.substring(colonIndex + 2, mail.length() - 2);
        User user = userService.requestPasswordReset(email).orElseThrow(EmailNotFoundException::new);
        user.setLangKey("fa");
        mailService.sendPasswordResetMail(user);
    }

    /**
     * POST   /account/reset-password/finish : Finish to reset the password of the user
     *
     * @param keyAndPassword the generated key and the new password
     * @throws InvalidPasswordException 400 (Bad Request) if the password is incorrect
     * @throws RuntimeException 500 (Internal Server Error) if the password could not be reset
     */
    @PostMapping(path = "/account/reset-password/finish")
    @Timed
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        if (keyAndPassword.getKey() == null) {
            throw new BadRequestAlertException("key should not be null", ENTITY_NAME, "null key");
        }
        Optional<User> user =
            userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new InternalServerErrorException("No user was found for this reset key");
        }
    }

    @PostMapping("/authenticate")
    @Timed
    public ResponseEntity<UserJWTController.JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {

        User userToLogIn = userService.getUserWithAuthoritiesByEmail(loginVM.getUsername()).orElse(null);

        if (userToLogIn == null) {
            throw new InternalServerErrorException("No user was found for this email");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(userToLogIn.getLogin(), loginVM.getPassword());

        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
        Optional<User> user = userService.getUserWithAuthoritiesByLogin(userToLogIn.getLogin());
        long userId = user.get().getId();
        String jwt = tokenProvider.createToken(authentication, rememberMe);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        httpHeaders.add("userId", String.valueOf(userId));
        return new ResponseEntity<>(new UserJWTController.JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    public static class JWTToken {

        private String idToken;

        public JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }

    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
            password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
            password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }
}
