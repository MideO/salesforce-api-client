package com.mideo.salesforce;


public class SalesforceConfig {
    String loginUrl;
    String clientId;
    String clientSecret;
    String username;
    String password;
    String token;

    public SalesforceConfig(String loginUrl,
                            String clientId,
                            String clientSecret,
                            String username,
                            String password,
                            String token) {
        this.loginUrl = loginUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.username = username;
        this.password = password;
        this.token = token;
    }
}
