package org.javawebstack.passport.services.oauth2;

import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.httpclient.HTTPClient;
import org.javawebstack.httpserver.Exchange;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class InteraAppsOAuth2Service extends HTTPClient implements OAuth2Service {
    private final String clientId;
    private final String secret;
    private String[] scopes;
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

    public OAuth2Callback callback(Exchange exchange){
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
            AbstractObject userData = get("/user")
                            .header("x-auth-key", accessToken)
                            .data().object();
            OAuth2Callback.Profile profile = new OAuth2Callback.Profile();
            System.out.println(userData.toJson());
            if (userData.has("id"))
                profile.id = userData.get("id").number().toString();
            if (userData.has("name"))
                profile.name = userData.get("name").string();
            if (userData.has("mail"))
                profile.mail = userData.get("mail").string();
            if (userData.has("profile_picture"))
                profile.avatar = userData.get("profile_picture").string();
            return new OAuth2Callback(accessToken, profile);
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


    public String getName() {
        return "interaapps";
    }


}
