package org.javawebstack.passport.services.oauth2;

import com.google.gson.annotations.SerializedName;
import jdk.jfr.Name;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.abstractdata.util.QueryString;
import org.javawebstack.httpclient.HTTPClient;
import org.javawebstack.httpserver.Exchange;
import org.javawebstack.httpserver.helper.MimeType;
import org.javawebstack.passport.models.PassportUser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class GithubOAuth2Service extends HTTPClient implements OAuth2Service {
    private final String clientId;
    private final String secret;
    private String[] scopes = new String[]{"read:user","user:email"};
    private String redirectDomain;
    private UserTransformer userTransformer;

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

    public PassportUser callback(Exchange exchange) {
        AbstractObject abstractObject = new HTTPClient("https://github.com").post("/login/oauth/access_token")
                .formBodyString(new QueryString()
                        .set("client_id", clientId)
                        .set("client_secret", secret)
                        .set("code", exchange.rawRequest().getParameter("code"))
                        .toString())
                .header("Accept", MimeType.JSON.getMimeTypes().get(0))
                .data().object();

        if (abstractObject.has("scope")/* && abstractObject.get("scope").string().equals("read:user,user:email")*/) {
            User user = get("/user")
                    .header("Authorization", "token "+abstractObject.get("access_token"))
                    .object(User.class);

            get("/user/emails")
                    .header("Authorization", "token "+abstractObject.get("access_token"))
                    .data().array().forEach(abstractElement -> {
                if (user.mail == null)
                    user.mail = abstractElement.object().get("email").string();
            });

            userTransformer.transform(user);

        }

        return null;
    }


    public Object redirect(Exchange exchange) {
        try {
            exchange.redirect("https://github.com/login/oauth/authorize?client_id="+clientId+"&scope="+ URLEncoder.encode(String.join(" ", scopes), "UTF-8")+"&redirect_uri="+URLEncoder.encode(redirectDomain+"/authorization/oauth2/"+getName()+"/callback", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return "";
    }

    public GithubOAuth2Service userTransformer(UserTransformer userTransformer){
        this.userTransformer = userTransformer;
        return this;
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

    public interface UserTransformer {
        void transform(User user);
    }
}
