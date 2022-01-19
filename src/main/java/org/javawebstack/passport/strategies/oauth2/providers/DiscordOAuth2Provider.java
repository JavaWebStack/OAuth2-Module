package org.javawebstack.passport.strategies.oauth2.providers;

import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.abstractdata.util.QueryString;
import org.javawebstack.httpclient.HTTPClient;
import org.javawebstack.passport.strategies.oauth2.OAuth2Profile;
import org.javawebstack.passport.strategies.oauth2.OAuth2Provider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DiscordOAuth2Provider extends OAuth2Provider {
    private String clientId;
    private String secret;
    private String[] scopes = {"read:user","user:email"};
    private HTTPClient discordClient;

    public DiscordOAuth2Provider(String clientId, String secret){
        this.clientId = clientId;
        this.secret = secret;
        discordClient = new HTTPClient("https://discord.com");
    }

    public DiscordOAuth2Provider setScopes(String... scopes) {
        this.scopes = scopes;
        return this;
    }

    public OAuth2Callback callback(AbstractObject queryParameters, String callbackUrl) {
        AbstractObject abstractObject = discordClient.post("/api/oauth2/token")
                .header("User-Agent", "JWSPassportClient/1")
                .formBodyString(new QueryString()
                        .set("client_id", clientId)
                        .set("client_secret", secret)
                        .set("code", queryParameters.string("code"))
                        .set("grant_type", "authorization_code")
                        .set("redirect_uri", callbackUrl)
                        .set("scope", String.join(" ", scopes))
                        .toString()
                )
                .data().object();
        return new OAuth2Callback(abstractObject.string("access_token"), abstractObject.string("refresh_token"));
    }

    public String redirect(String callbackUrl) {
        try {
            return "https://github.com/login/oauth/authorize?client_id="+clientId+"&scope="+ URLEncoder.encode(String.join(" ", scopes), "UTF-8")+"&redirect_uri="+URLEncoder.encode(callbackUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public GitHubOAuth2Provider.OAuth2Callback refreshToken(AbstractObject queryParameters) {
        return null;
    }

    public GitHubOAuth2Provider.OAuth2Callback getFromToken(AbstractObject queryParameters) {
        return null;
    }

    public static class OAuth2Callback extends org.javawebstack.passport.strategies.oauth2.OAuth2Callback {
        public OAuth2Callback(String accessToken, String refreshToken) {
            super(accessToken, refreshToken);
        }

        public HTTPClient createApiClient() {
            return new HTTPClient("https://discord.com/").bearer(accessToken);
        }

        public OAuth2Profile getProfile() {
            OAuth2Profile profile = new OAuth2Profile();

            AbstractObject data = createApiClient().get("/api/users/@me")
                            .bearer(accessToken)
                            .header("User-Agent", "JWSPassportClient/1")
                            .data().object();

            if (data.has("id")) {
                profile.id = data.get("id").string();

                if (data.has("avatar"))
                    profile.avatar = "https://cdn.discordapp.com/avatars/"+profile.id+"/"+data.get("avatar").string()+".png";
            }

            if (data.has("username"))
                profile.name = data.get("username").string();

            if (data.has("email"))
                profile.mail = data.get("email").string();

            data.forEach(profile::set);

            return profile;
        }
    }
}
