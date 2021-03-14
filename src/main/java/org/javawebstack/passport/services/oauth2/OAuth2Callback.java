package org.javawebstack.passport.services.oauth2;

import org.javawebstack.httpclient.HTTPClient;
import org.javawebstack.passport.Profile;

public class OAuth2Callback {
    private String token;
    private Profile profile;
    private HTTPClient httpClient;
    private String refreshToken;

    public OAuth2Callback(String token, Profile profile, HTTPClient httpClient) {
        this.token = token;
        this.profile = profile;
        this.httpClient = httpClient;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getToken() {
        return token;
    }

    public HTTPClient getHttpClient() {
        return httpClient;
    }

    public OAuth2Callback setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

}
