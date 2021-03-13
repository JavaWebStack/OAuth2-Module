package org.javawebstack.passport;

import org.javawebstack.httpserver.HTTPServer;

public interface AuthService {
    String getName();

    default void setupServer(HTTPServer server){}
}
