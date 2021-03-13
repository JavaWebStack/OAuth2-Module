package org.javawebstack.passport.services.oauth2;

import com.google.gson.annotations.SerializedName;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.httpclient.HTTPClient;
import org.javawebstack.httpserver.Exchange;
import org.javawebstack.passport.models.PassportUser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InteraAppsOAuth2Service extends HTTPClient implements OAuth2Service {
    private final String clientId;
    private final String secret;
    private String[] scopes;
    private UserTransformer userTransformer = (user)->{};
    private String redirectDomain;

    public InteraAppsOAuth2Service(String clientId, String secret, String redirectDomain){
        this.redirectDomain = redirectDomain;
        setBaseUrl("https://accounts.interaapps.de/api/v2");
        this.clientId = clientId;
        this.secret = secret;
        scopes = new String[]{"user:read"};
    }

    public InteraAppsOAuth2Service setScopes(String[] scopes) {
        this.scopes = scopes;
        return this;
    }

    public PassportUser callback(Exchange exchange){
        AbstractObject data = post("/authorization/oauth2/access_token")
                .jsonBodyElement(new AbstractObject()
                        .set("client_id", clientId)
                        .set("client_secret", secret)
                        .set("code", exchange.rawRequest().getParameter("code"))
                )
                .data()
                .object();

        List<String> scopes = Arrays.asList(this.scopes);

        for (AbstractElement scope : data.get("scope_list").array()) {
            if (!scopes.contains(scope.string()))
                return null;
        }

        if (data.get("success").bool()) {

            System.out.println("YE2");
            String accessToken = data.get("access_token").string();

            System.out.println("TRANSFORMING");
            userTransformer.transform(get("/user")
                            .header("x-auth-key", accessToken)
                            .object(User.class));
        }

        return null;
    }

    @Override
    public Object redirect(Exchange exchange) {
        try {
            exchange.redirect("https://accounts.interaapps.de/auth/oauth2?client_id="+clientId+"&scope="+URLEncoder.encode(String.join(" ", scopes), "UTF-8")+"&redirect_uri="+URLEncoder.encode(redirectDomain+"/authorization/oauth2/"+getName()+"/callback", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return "";
    }

    public InteraAppsOAuth2Service userTransformer(UserTransformer userTransformer){
        this.userTransformer = userTransformer;
        return this;
    }

    public String getName() {
        return "interaapps";
    }

    public static class User {
        private int id;
        private String name;
        private String mail;
        private String birthday;
        @SerializedName("favorite_color")
        private String favoriteColor;
        private String description;
        @SerializedName("profile_picture")
        private String profilePicture;

        public int getId() {
            return id;
        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public String getName() {
            return name;
        }

        public String getBirthday() {
            return birthday;
        }

        public String getDescription() {
            return description;
        }

        public String getFavoriteColor() {
            return favoriteColor;
        }

        public String getMail() {
            return mail;
        }
    }

    public interface UserTransformer {
        void transform(User user);
    }

}
