package org.javawebstack.passport.strategies.oauth2;

import org.javawebstack.abstractdata.AbstractObject;

public abstract class OAuth2Provider {
    /**
     * @param queryParameters
     */
    public abstract OAuth2Callback callback(AbstractObject queryParameters, String callbackUrl);

    public abstract String redirect(String callbackUrl);

    public abstract OAuth2Callback refreshToken(AbstractObject queryParameters);
    public abstract OAuth2Callback getFromToken(AbstractObject queryParameters);

}
