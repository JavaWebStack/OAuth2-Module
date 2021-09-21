package org.javawebstack.passport.services.oauth2;

import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.abstractdata.util.QueryString;
import org.javawebstack.httpclient.HTTPClient;
import org.javawebstack.httpserver.Exchange;
import org.javawebstack.httpserver.helper.MimeType;
import org.javawebstack.passport.OAuth2Module;
import org.javawebstack.passport.Profile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class FacebookOAuth2Service extends HTTPClient implements OAuth2Service {
    private final String clientId;
    private final String secret;
    private String[] scopes = new String[]{"read:user","user:email"};
    private String redirectDomain;

    public FacebookOAuth2Service(String clientId, String secret, String redirectDomain){
        setBaseUrl("https://api.github.com");
        this.clientId = clientId;
        this.secret = secret;
        this.redirectDomain = redirectDomain;
    }

    public String getName() {
        return "github";
    }

    public FacebookOAuth2Service setScopes(String[] scopes) {
        this.scopes = scopes;
        return this;
    }

    public OAuth2Callback callback(Exchange exchange, OAuth2Module oAuth2Module) {

        AbstractObject abstractObject = new HTTPClient("https://github.com").post("/login/oauth/access_token")
                .formBodyString(new QueryString()
                        .set("client_id", clientId)
                        .set("client_secret", secret)
                        .set("code", exchange.rawRequest().getParameter("code"))
                        .toString())
                .header("Accept", MimeType.JSON.getMimeTypes().get(0))
                .data().object();


        if (abstractObject.has("scope")/* && abstractObject.get("scope").string().equals("read:user,user:email")*/) {
            Profile profile = new Profile();
            return new OAuth2Callback(abstractObject.get("access_token").string(), getProfile(abstractObject.get("access_token").string()), new HTTPClient("https://api.github.com").header("Authorization", "token "+abstractObject.get("access_token").string()));
        }

        return null;
    }


    public Object redirect(Exchange exchange, OAuth2Module oAuth2Module) {
        try {
            exchange.redirect("https://www.facebook.com/v11.0/dialog/oauth?client_id="+clientId+"&scope="+ URLEncoder.encode(String.join(" ", scopes), "UTF-8")+"&redirect_uri="+URLEncoder.encode(redirectDomain+oAuth2Module.getPathPrefix()+getName()+"/callback", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return "";
    }

    @Override
    public Profile getProfile(String accessToken) {
        Profile profile = new Profile();
        AbstractObject userData = get("/user")
                .header("Authorization", "token "+accessToken)
                .data().object();

        if (userData.has("id"))
            profile.id = userData.get("id").number().toString();
        if (userData.has("name"))
            profile.name = userData.get("name").string();
        if (userData.has("avatar_url"))
            profile.avatar = userData.get("avatar_url").string();

        userData.forEach(profile::set);

        get("/user/emails")
                .header("Authorization", "token "+accessToken)
                .data().array().forEach(abstractElement -> {
            if (profile.mail == null) {
                profile.mail = abstractElement.object().get("email").string();
            }
        });
        return profile;
    }


}
