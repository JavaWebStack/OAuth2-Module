package org.javawebstack.passport.strategies.oauth2;

import org.javawebstack.httpclient.HTTPClient;

public abstract class OAuth2Callback {
    protected String accessToken;
    protected String refreshToken;

    public OAuth2Callback(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public abstract HTTPClient createApiClient();

    public abstract OAuth2Profile getProfile();

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public abstract Class<? extends OAuth2Provider> getProviderClass();
}
