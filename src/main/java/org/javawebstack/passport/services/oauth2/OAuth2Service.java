package org.javawebstack.passport.services.oauth2;

import org.javawebstack.httpserver.Exchange;
import org.javawebstack.passport.AuthService;
import org.javawebstack.passport.models.PassportUser;

public interface OAuth2Service extends AuthService {
    PassportUser callback(Exchange exchange);

    Object redirect(Exchange exchange);
}
