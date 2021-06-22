package org.javawebstack.passport.services.oauth2;

import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.abstractdata.util.QueryString;
import org.javawebstack.httpclient.HTTPClient;
import org.javawebstack.httpserver.Exchange;
import org.javawebstack.httpserver.helper.MimeType;
import org.javawebstack.passport.Profile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TwitchOAuth2Service extends HTTPClient implements OAuth2Service {
    private final String clientId;
    private final String clientSecret;
    private String[] scopes = new String[]{"user:read:email"}; // reference: https://dev.twitch.tv/docs/authentication/#scopes
    private final String redirectUri;
    private boolean forceVerify = false;
    private String state;

    public TwitchOAuth2Service(String clientId, String redirectUri, String clientSecret) {
        setBaseUrl("https://api.twitch.tv/helix");
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.clientSecret = clientSecret;
    }

    public TwitchOAuth2Service setState(String state) {
        this.state = state;
        return this;
    }

    public String getState() {
        return state;
    }

    public TwitchOAuth2Service setScopes(String[] scopes) {
        this.scopes = scopes;
        return this;
    }

    public TwitchOAuth2Service setForceVerify(boolean forceVerify) {
        this.forceVerify = forceVerify;
        return this;
    }

    public boolean isForceVerify() {
        return forceVerify;
    }

    public String getName() {
        return "twitch";
    }

    public OAuth2Callback callback(Exchange exchange) {
        AbstractObject abstractObject = new HTTPClient("https://id.twitch.tv").post("/oauth2/token")
                .formBodyString(new QueryString()
                    .set("client_id", clientId)
                    .set("client_secret", clientSecret)
                    .set("code", exchange.rawRequest().getParameter("code"))
                    .set("grant_type", "authorization_code")
                    .set("redirect_uri", redirectUri)
                    .toString())
                .header("Accept", MimeType.JSON.getMimeTypes().get(0))
                .data().object();

        if (abstractObject.has("scope")) {
            String accessToken = abstractObject.get("access_token").string();
            return new OAuth2Callback(accessToken, getProfile(accessToken), new HTTPClient("https://api.twitch.tv/helix").bearer(accessToken));
        }

        return null;
    }

    public Object redirect(Exchange exchange, String redirectPathPrefix) {
        try {
            exchange.redirect("https://id.twitch.tv/oauth2/authorize?client_id="+clientId+"&response_type=code&scope="+ URLEncoder.encode(String.join(" ", scopes), "UTF-8")+"&redirect_uri="+URLEncoder.encode(redirectUri+redirectPathPrefix+getName()+"/callback", "UTF-8")+"&force_verify="+forceVerify+"&state="+state);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return "";
    }

    public Profile getProfile(String accessToken) {
        Profile profile = new Profile();

        AbstractObject userData = get("/users")
                .bearer(accessToken)
                .data().object().get("data").array().get(0).object();

        if (userData.has("id"))
            profile.id = userData.get("id").number().toString();
        if (userData.has("login"))
            profile.name = userData.get("login").string();
        if (userData.has("profile_image_url"))
            profile.avatar = userData.get("profile_image_url").string();
        if (userData.has("email"))
            profile.mail = userData.get("email").string();
        userData.forEach(profile::set);

        return profile;
    }
}
