<p align="center"><img src="https://raw.githubusercontent.com/JavaWebStack/docs/master/docs/assets/img/icon.svg" width="100">
<br><br>
JWS-Passport
</p>

![GitHub Workflow Status (branch)](https://img.shields.io/github/workflow/status/JavaWebStack/Passport/Maven%20Deploy/master)
![GitHub](https://img.shields.io/github/license/JavaWebStack/Passport)
![Lines of code](https://img.shields.io/tokei/lines/github/JavaWebStack/Passport)
![Discord](https://img.shields.io/discord/815612319378833408?color=%237289DA&label=discord)
![Twitter Follow](https://img.shields.io/twitter/follow/JavaWebStack?style=social)

## Introduction
Passport is a JWS-Module which allows you to create easily Authentication in your WebApp. The OAuth2 Support gives you a unified way of implementing OAuth2-Authorization in your WebApp

### Example usage

```java
class MyApp {
    /* ... */
    protected void setup() {
        HTTPServer httpServer = new HTTPServer().port(1234);

        Passport passport = new Passport("/auth");
        OAuth2Strategy oAuth2Strategy = new OAuth2Strategy("http://localhost:1234");
        oAuth2Strategy.setHttpCallbackHandler((e, callback, name) -> {
            return "Hello " + callback.getProfile().getName();
        });

        oAuth2Strategy.use("google", new GoogleOAuth2Provider("myid", "mysecret").setScopes("https://www.googleapis.com/auth/userinfo.profile", "https://www.googleapis.com/auth/userinfo.email"));
        oAuth2Strategy.use("interaapps", new InteraAppsOAuth2Provider("myid", "mysecret").setScopes("user:read"));

        passport.use("oauth2", oAuth2Strategy);

        passport.createRoutes(httpServer);
        httpServer.start();

        // Creates Routes: /auth/oauth2/interaapps, /auth/oauth2/interaapps/callback
    }


    // JWS-Passport ships also an abstracted form of handling oauth2 
    public void oAuthWithoutHTTPServer() {
        OAuth2Strategy oAuth2Strategy = new OAuth2Strategy("http://localhost:1234");
        oAuth2Strategy.use("interaapps", new InteraAppsOAuth2Provider("myid", "mysecret").setScopes("user:read"));

        // Redirect
        String callbackUrl = ".../callback";
        String redirectUrl = oAuth2Strategy.get("interaapps").redirect(callbackUrl);

        // On callback
        OAuth2Callback callback = oAuth2Strategy.get("interaapps").callback(new AbstractObject().set("code", code), callbackUrl);
        System.out.println("Hello " + callback.getProfile().name);
    }

    /* ... */
}
```

### Dependency
```xml
<dependency>
    <groupId>org.javawebstack</groupId>
    <artifactId>Passport</artifactId>
    <version>1.0-SNAPSHOT<!-- VERSION --></version>
</dependency>
```

# Services
Service|Class|Control-Panel|More Information
---|---|---|---
Github|GithubOAuth2Provider|[Github Developer Center](https://github.com/settings/developers)|-
Google|GoogleOAuth2Provider|[Google Developer Console](https://console.developers.google.com/)|-
Discord|DiscordOAuth2Provider|[Discord Developer Portal](https://discord.com/developers/applications)|-
Facebook|FacebookOAuth2Provider|[Facebook Developer Center](https://console.developers.google.com/)|TODO
InteraApps|InteraAppsOAuth2Provider|[IA-Accounts Developer Center](https://accounts.interaapps.de/developers/projects)|-
Twitch|TwitchOAuth2Provider|[Twitch Developers](https://dev.twitch.tv/)|Implements the OAuth authorization code flow
