package org.javawebstack.passport.strategies.oauth2;

import org.javawebstack.httpserver.Exchange;
import org.javawebstack.httpserver.HTTPServer;
import org.javawebstack.passport.strategies.Strategy;

import java.util.HashMap;
import java.util.Map;

public class OAuth2Strategy extends Strategy {
    private Map<String, OAuth2Provider> providers = new HashMap<>();
    private HttpCallbackHandler httpCallbackHandler;
    private String host;


    public OAuth2Strategy(String host){

        this.host = host;
    }

    public OAuth2Strategy use(String name, OAuth2Provider oAuth2Provider){
        providers.put(name, oAuth2Provider);
        return this;
    }

    public void createRoutes(String prefixUrl, HTTPServer httpServer) {
        providers.forEach((name, oauth2) -> {
            final String callbackUrl = prefixUrl+"/"+name+"/callback";
            httpServer.get(prefixUrl+"/"+name, e -> {
                e.redirect(oauth2.redirect(host+callbackUrl));
                return "";
            });

            httpServer.get(callbackUrl, e -> {
                return httpCallbackHandler.handle(e, oauth2.callback(e.getQueryParameters(), host+callbackUrl));
            });
        });
    }

    public void setHttpCallbackHandler(HttpCallbackHandler httpCallbackHandler) {
        this.httpCallbackHandler = httpCallbackHandler;
    }

    public interface HttpCallbackHandler {
        Object handle(Exchange exchange, OAuth2Callback callback);
    }
}
