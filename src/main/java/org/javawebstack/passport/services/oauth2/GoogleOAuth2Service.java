package org.javawebstack.passport.services.oauth2;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import com.google.gson.annotations.SerializedName;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.abstractdata.util.QueryString;
import org.javawebstack.httpclient.HTTPClient;
import org.javawebstack.httpserver.Exchange;
import org.javawebstack.httpserver.helper.MimeType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;


public class GoogleOAuth2Service extends HTTPClient implements OAuth2Service {
    private final String clientId;
    private final String secret;
    private String redirectDomain;
    private GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;

    public GoogleOAuth2Service(String clientId, String secret, String redirectDomain){
        setBaseUrl("https://api.github.com");
        this.clientId = clientId;
        this.secret = secret;
        this.redirectDomain = redirectDomain;
        googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                clientId,
                secret,
                Arrays.asList(
                        "https://www.googleapis.com/auth/userinfo.profile",
                        "https://www.googleapis.com/auth/userinfo.email"
                )
        ).build();
    }

    public String getName() {
        return "google";
    }

    public GoogleOAuth2Service setScopes(String[] scopes) {
        for (String scope : scopes) {
            googleAuthorizationCodeFlow.getScopes().add(scope);
        }
        return this;
    }

    public OAuth2Callback callback(Exchange exchange) {
        GoogleTokenResponse code = null;
        try {
            code = googleAuthorizationCodeFlow.newTokenRequest(exchange.rawRequest().getParameter("code")).execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        OAuth2Callback.Profile profile = getProfile(code.getAccessToken());
        if (profile != null)
            return new OAuth2Callback(code.getAccessToken(), profile, new HTTPClient("https://oauth2.googleapis.com").bearer(code.getAccessToken()));

        return null;
    }


    public Object redirect(Exchange exchange, String redirectPathPrefix) {
        exchange.redirect(
                googleAuthorizationCodeFlow
                    .newAuthorizationUrl()
                    .setAccessType("offline")
                    .setRedirectUri(redirectDomain+redirectPathPrefix+"/"+getName()+"/callback")
                    .build()
        );
        return "";
    }

    @Override
    public OAuth2Callback.Profile getProfile(String accessToken) {
        Credential credential = new GoogleCredential.Builder()
                .setTransport(new NetHttpTransport())
                .setJsonFactory(GsonFactory.getDefaultInstance())
                .setClientSecrets(clientId, secret)
                .build().setAccessToken(accessToken);

        Oauth2 oauth2 = new Oauth2.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                credential
        ).setApplicationName(clientId).build();
        try {
            OAuth2Callback.Profile profile = new OAuth2Callback.Profile();
            Userinfo userinfo = oauth2.userinfo().get().execute();
            profile.id     = userinfo.getId();
            profile.name   = userinfo.getName();
            profile.avatar = userinfo.getPicture();
            profile.mail   = userinfo.getEmail();
            return profile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
