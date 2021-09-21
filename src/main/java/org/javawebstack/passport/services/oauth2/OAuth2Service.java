package org.javawebstack.passport.services.oauth2;

import org.javawebstack.httpserver.Exchange;
import org.javawebstack.passport.AuthService;
import org.javawebstack.passport.OAuth2Module;
import org.javawebstack.passport.Profile;

public interface OAuth2Service extends AuthService {
    OAuth2Callback callback(Exchange exchange, OAuth2Module oAuth2Module);

    Object redirect(Exchange exchange, OAuth2Module oAuth2Module);

    Profile getProfile(String accessToken);
}
