package org.javawebstack.passport.strategies.oauth2;

import org.javawebstack.abstractdata.AbstractObject;

public abstract class OAuth2Provider {
    public abstract OAuth2Callback callback(AbstractObject queryParameters, String callbackUrl);

    public abstract String redirect(String callbackUrl);
}
