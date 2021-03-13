package org.javawebstack.passport;

import org.javawebstack.framework.WebApplication;
import org.javawebstack.framework.module.Module;
import org.javawebstack.httpserver.HTTPServer;
import org.javawebstack.injector.Injector;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.wrapper.SQL;
import org.javawebstack.passport.services.oauth2.OAuth2Callback;
import org.javawebstack.passport.services.oauth2.OAuth2CallbackHandler;
import org.javawebstack.passport.services.oauth2.OAuth2Service;

import java.util.ArrayList;
import java.util.List;

public class OAuth2Module implements Module {

    public List<AuthService> services;

    private OAuth2CallbackHandler oAuth2Callback = (exchange, callback)->null;

    public OAuth2Module(){
        services = new ArrayList<>();
    }

    public void setupServer(WebApplication application, HTTPServer server) {
        services.forEach(service -> {
            if (service instanceof OAuth2Service) {
                server.get("/authorization/oauth2/"+service.getName(), ((OAuth2Service) service)::redirect);
                server.get("/authorization/oauth2/"+service.getName()+"/callback", exchange -> {
                    OAuth2Callback callback = ((OAuth2Service) service).callback(exchange);
                    return oAuth2Callback.callback(exchange, callback);
                });
            }
        });
    }

    public void setupInjection(WebApplication application, Injector injector) {

    }

    public void setupModels(WebApplication application, SQL sql) throws ORMConfigurationException {

    }

    public OAuth2Module addService(AuthService authService) {
        services.add(authService);
        return this;
    }

    public void setOAuth2Callback(OAuth2CallbackHandler oAuth2Callback) {
        this.oAuth2Callback = oAuth2Callback;
    }
}
