package org.javawebstack.passport.services.oauth2;

import com.google.gson.Gson;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.abstractdata.util.QueryString;
import org.javawebstack.httpclient.HTTPClient;
import org.javawebstack.httpserver.Exchange;
import org.javawebstack.passport.OAuth2Module;
import org.javawebstack.passport.Profile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class DiscordOAuth2Service extends HTTPClient implements OAuth2Service {
    private final String clientId;
    private final String secret;
    private String[] scopes = new String[]{"email", "identify"};
    private String redirectDomain;

    public DiscordOAuth2Service(String clientId, String secret, String redirectDomain){
        setBaseUrl("https://discord.com");
        this.clientId = clientId;
        this.secret = secret;
        this.redirectDomain = redirectDomain;
    }

    public String getName() {
        return "discord";
    }

    public DiscordOAuth2Service setScopes(String[] scopes) {
        this.scopes = scopes;
        return this;
    }

    public OAuth2Callback callback(Exchange exchange, OAuth2Module oAuth2Module) {
        AbstractObject abstractObject = post("/api/oauth2/token")
                .header("User-Agent", "JWSPassportClient/1")
                .formBodyString(new QueryString()
                        .set("client_id", clientId)
                        .set("client_secret", secret)
                        .set("code", exchange.rawRequest().getParameter("code"))
                        .set("grant_type", "authorization_code")
                        .set("redirect_uri", redirectDomain+oAuth2Module.getPathPrefix()+getName()+"/callback")
                        .set("scope", String.join(" ", scopes))
                        .toString()
                )
                .data().object();

        return new OAuth2Callback(
                abstractObject.get("access_token").string(),
                getProfile(abstractObject.get("access_token").string()),
                new HTTPClient("https://discord.com/").bearer(abstractObject.get("access_token").string())).setRefreshToken(abstractObject.get("refresh_token").string());
    }


    public Object redirect(Exchange exchange, OAuth2Module oAuth2Module) {
        try {
            exchange.redirect("https://discord.com/api/oauth2/authorize?response_type=code&client_id="+clientId+"&prompt=consent&scope="+ URLEncoder.encode(String.join(" ", scopes), "UTF-8")+"&redirect_uri="+URLEncoder.encode(redirectDomain+oAuth2Module.getPathPrefix()+getName()+"/callback", "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return "";
    }

    @Override
    public Profile getProfile(String accessToken) {
        Profile profile = new Profile();

        AbstractObject data =
                get("/api/users/@me")
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
