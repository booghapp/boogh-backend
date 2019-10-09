package org.boogh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Boogh.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    public final Aws aws = new Aws();

    public final Telegram telegram = new Telegram();

    public final Redis redis = new Redis();

    public final Twitter twitter = new Twitter();

    public final Google google = new Google();

    public Aws getAws() {
        return aws;
    }

    public Telegram getTelegram() { return telegram;}

    public Redis getRedis() {
        return redis;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public Google getGoogle() {
        return google;
    }

    public static class Aws {

        private String backendS3BucketName = "";

        private String backendAccessKeyId = "";

        private String backendSecretAccessKey = "";

        private String backendS3Location = "";

        public String getBackendS3BucketName() {
            return backendS3BucketName;
        }

        public void setBackendS3BucketName(String backendS3BucketName) {
            this.backendS3BucketName = backendS3BucketName;
        }

        public String getBackendAccessKeyId() {
            return backendAccessKeyId;
        }

        public void setBackendAccessKeyId(String backendAccessKeyId) {
            this.backendAccessKeyId = backendAccessKeyId;
        }

        public String getBackendSecretAccessKey() {
            return backendSecretAccessKey;
        }

        public void setBackendSecretAccessKey(String backendSecretAccessKey) {
            this.backendSecretAccessKey = backendSecretAccessKey;
        }

        public String getBackendS3Location() {
            return backendS3Location;
        }

        public void setBackendS3Location(String backendS3Location) {
            this.backendS3Location = backendS3Location;
        }

    }

    public static class Telegram {

        private String botSecret = "";

        private String botToken = "";

        public String getBotToken() {
            return botToken;
        }

        public void setBotToken(String botToken) {
            this.botToken = botToken;
        }

        public String getBotSecret() {
            return botSecret;
        }

        public void setBotSecret(String botSecret) {
            this.botSecret = botSecret;
        }
    }

    public static class Redis {

        private String password = "";

        private String address = "";

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

    }

    public static class Twitter {

        private String apiKey = "";

        private String apiSecret = "";

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getApiSecret() {
            return apiSecret;
        }

        public void setApiSecret(String apiSecret) {
            this.apiSecret = apiSecret;
        }
    }

    public static class Google {

        private String clientId = "";

        private String clientSecret = "";

        private String mapsApiKey = "";

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getMapsApiKey() {
            return mapsApiKey;
        }

        public void setMapsApiKey(String mapsApiKey) {
            this.mapsApiKey = mapsApiKey;
        }

    }
}
