package org.javawebstack.passport.services.oauth2;

import org.javawebstack.httpclient.HTTPClient;
import org.javawebstack.httpserver.Exchange;

public interface OAuth2CallbackHandler {
    Object callback(String service, Exchange exchange, OAuth2Callback callback, String accessToken, HTTPClient httpClient);
}
