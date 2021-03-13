package org.javawebstack.passport.services.oauth2;

import com.google.gson.annotations.SerializedName;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.abstractdata.util.QueryString;
import org.javawebstack.httpclient.HTTPClient;
import org.javawebstack.httpserver.Exchange;
import org.javawebstack.httpserver.helper.MimeType;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class GithubOAuth2Service extends HTTPClient implements OAuth2Service {
    private final String clientId;
    private final String secret;
    private String[] scopes = new String[]{"read:user","user:email"};
    private String redirectDomain;

    public GithubOAuth2Service(String clientId, String secret, String redirectDomain){
        setBaseUrl("https://api.github.com");
        this.clientId = clientId;
        this.secret = secret;
        this.redirectDomain = redirectDomain;
    }

    public String getName() {
        return "github";
    }

    public GithubOAuth2Service setScopes(String[] scopes) {
        this.scopes = scopes;
        return this;
    }

    public OAuth2Callback callback(Exchange exchange) {

        AbstractObject abstractObject = new HTTPClient("https://github.com").post("/login/oauth/access_token")
                .formBodyString(new QueryString()
                        .set("client_id", clientId)
                        .set("client_secret", secret)
                        .set("code", exchange.rawRequest().getParameter("code"))
                        .toString())
                .header("Accept", MimeType.JSON.getMimeTypes().get(0))
                .data().object();


        if (abstractObject.has("scope")/* && abstractObject.get("scope").string().equals("read:user,user:email")*/) {
            OAuth2Callback.Profile profile = new OAuth2Callback.Profile();
            AbstractObject userData = get("/user")
                    .header("Authorization", "token "+abstractObject.get("access_token"))
                    .data().object();

            if (userData.has("id"))
                profile.id = userData.get("id").number().toString();
            if (userData.has("name"))
                profile.name = userData.get("name").string();
            if (userData.has("avatar_url"))
                profile.avatar = userData.get("avatar_url").string();

            get("/user/emails")
                    .header("Authorization", "token "+abstractObject.get("access_token"))
                    .data().array().forEach(abstractElement -> {
                if (profile.mail == null) {
                    profile.mail = abstractElement.object().get("email").string();
                }
            });

            return new OAuth2Callback(abstractObject.get("access_token").string(), profile, new HTTPClient("https://api.github.com").header("Authorization", "token "+abstractObject.get("access_token").string()));
        }

        return null;
    }


    public Object redirect(Exchange exchange, String redirectPathPrefix) {
        try {
            exchange.redirect("https://github.com/login/oauth/authorize?client_id="+clientId+"&scope="+ URLEncoder.encode(String.join(" ", scopes), "UTF-8")+"&redirect_uri="+URLEncoder.encode(redirectDomain+redirectPathPrefix+"/"+getName()+"/callback", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return "";
    }


    public static class User {
        private String id;
        @SerializedName("login")
        private String name;
        private String mail;
        @SerializedName("avatar_url")
        private String profilePicture;

        public String getMail() {
            return mail;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }
    }

}
