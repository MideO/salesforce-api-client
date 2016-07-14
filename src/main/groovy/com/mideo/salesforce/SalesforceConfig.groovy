package com.mideo.salesforce;


class SalesforceConfig {
    String loginUrl;
    String clientId;
    String clientSecret;
    String user;
    String password;
    String token;

    public SalesforceConfig(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public SalesforceConfig clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public SalesforceConfig clientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public SalesforceConfig userName(String user) {
        this.user = user;
        return this;
    }

    public SalesforceConfig password(String password) {
        this.password = password;
        return this;
    }

    public SalesforceConfig userToken(String token) {
        this.token = token;
        return this;
    }

    @Override
    public String toString() {

        String config = "grant_type=password&client_id=%s&client_secret=%s&username=%s&password=%s";
        try {
            config = String.format(config, clientId, clientSecret, URLEncoder.encode(user, "UTF-8"), password + token);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return config;
    }
}

