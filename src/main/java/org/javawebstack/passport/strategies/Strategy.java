package org.javawebstack.passport.strategies;

import org.javawebstack.httpserver.HTTPServer;
import org.javawebstack.passport.Passport;

public abstract class Strategy {
    protected String name;
    protected Passport passport;

    public abstract void createRoutes(String prefixUrl, HTTPServer httpServer);

    public String getName() {
        return name;
    }

    public Passport getPassport() {
        return passport;
    }

    public void setPassport(Passport passport) {
        this.passport = passport;
    }
}
