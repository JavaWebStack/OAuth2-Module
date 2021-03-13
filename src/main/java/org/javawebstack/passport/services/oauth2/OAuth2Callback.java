package org.javawebstack.passport.services.oauth2;

import org.javawebstack.httpclient.HTTPClient;

public class OAuth2Callback {
    private String token;
    private Profile profile;
    private HTTPClient httpClient;

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

    public static class Profile {
        public String id;
        public String name;
        public String mail;
        public String avatar;
    }
}
