package org.javawebstack.passport;

import org.javawebstack.framework.WebApplication;
import org.javawebstack.framework.module.Module;
import org.javawebstack.httpserver.HTTPServer;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.wrapper.SQL;
import org.javawebstack.passport.services.oauth2.OAuth2Callback;
import org.javawebstack.passport.services.oauth2.OAuth2CallbackHandler;
import org.javawebstack.passport.services.oauth2.OAuth2Service;

import java.util.ArrayList;
import java.util.List;

public class OAuth2Module implements Module {

    private final List<AuthService> services;
    private String pathPrefix = "/authorization/oauth2/";

    private OAuth2CallbackHandler oAuth2Callback = (service, exchange, callback)->null;

    public OAuth2Module(){
        services = new ArrayList<>();
    }

    public void beforeSetupServer(WebApplication application, HTTPServer server) {
        services.forEach(service -> {
            service.setupServer(server);
            if (service instanceof OAuth2Service) {
                server.get(pathPrefix+service.getName(), exchange -> ((OAuth2Service) service).redirect(exchange, this));
                server.get(pathPrefix+service.getName()+"/callback", exchange -> {
                    OAuth2Callback callback = ((OAuth2Service) service).callback(exchange, this);

                    return oAuth2Callback.callback(service.getName(), exchange, callback);
                });
            }
        });
    }

    public void setupModels(WebApplication application, SQL sql) throws ORMConfigurationException {

    }

    public OAuth2Module addService(AuthService authService) {
        services.add(authService);
        return this;
    }

    public List<AuthService> getServices() {
        return services;
    }

    public void setOAuth2Callback(OAuth2CallbackHandler oAuth2Callback) {
        this.oAuth2Callback = oAuth2Callback;
    }

    public OAuth2Module setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
        return this;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }
}
