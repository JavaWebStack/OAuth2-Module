package org.javawebstack.passport.services.oauth2;

import org.javawebstack.httpserver.Exchange;
import org.javawebstack.passport.AuthService;

public interface OAuth2Service extends AuthService {
    OAuth2Callback callback(Exchange exchange);

    Object redirect(Exchange exchange, String redirectPathPrefix);
}
