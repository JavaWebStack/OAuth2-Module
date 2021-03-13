package org.javawebstack.passport.services.oauth2;

public class OAuth2Callback {
    private String token;
    private Profile profile;

    public OAuth2Callback(String token, Profile profile) {
        this.token = token;
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getToken() {
        return token;
    }

    public static class Profile {
        public String id;
        public String name;
        public String mail;
        public String avatar;
    }
}
