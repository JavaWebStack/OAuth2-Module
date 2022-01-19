package org.javawebstack.passport;

import org.javawebstack.httpserver.HTTPServer;
import org.javawebstack.passport.strategies.Strategy;
import org.javawebstack.passport.strategies.oauth2.OAuth2Provider;
import org.javawebstack.passport.strategies.oauth2.OAuth2Strategy;

import java.util.HashMap;
import java.util.Map;

public class Passport {

    private Map<String, Strategy> strategies = new HashMap<>();
    private String prefixUrl = "";

    public Passport() {}

    public Passport(String prefixUrl){
        this.prefixUrl = prefixUrl;
    }

    public Passport use(String name, Strategy strategy){
        strategy.setPassport(this);
        strategies.put(name, strategy);
        return this;
    }

    public Strategy get(String name){
        return strategies.get(name);
    }

    public Passport createRoutes(HTTPServer server){
        strategies.forEach((name, strategy) -> {
            strategy.createRoutes(prefixUrl+"/"+name, server);
        });
        return this;
    }

    public String getPrefixUrl() {
        return prefixUrl;
    }
}
