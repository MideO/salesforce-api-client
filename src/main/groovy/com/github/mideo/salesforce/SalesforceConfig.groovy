package com.github.mideo.salesforce;


class SalesforceConfig {
    String loginUrl;
    String user;
    String passwordAndToken;
    static String version = '36.0'

    public SalesforceConfig(String loginUrl) {
        this.loginUrl = loginUrl;
    }


    public SalesforceConfig userName(String user) {
        this.user = user;
        return this;
    }

    public SalesforceConfig passwordAndToken(String passwordAndToken) {
        this.passwordAndToken = passwordAndToken;
        return this;
    }

    public SalesforceConfig apiVersion(Double version) {
        this.version = version;
        return this;
    }

}

