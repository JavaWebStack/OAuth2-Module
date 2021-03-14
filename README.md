<p align="center"><img src="https://raw.githubusercontent.com/JavaWebStack/docs/master/docs/assets/img/icon.svg" width="100">
<br><br>
JWS-Passport
</p>

![GitHub Workflow Status (branch)](https://img.shields.io/github/workflow/status/JavaWebStack/Passport/Maven%20Deploy/master)
![GitHub](https://img.shields.io/github/license/JavaWebStack/Passport)
![Lines of code](https://img.shields.io/tokei/lines/github/JavaWebStack/ORM)
![Discord](https://img.shields.io/discord/815612319378833408?color=%237289DA&label=discord)
![Twitter Follow](https://img.shields.io/twitter/follow/JavaWebStack?style=social)

## Introduction
When it came to using an ORM Lib in java I used ORMLite before. It worked quite well, but I didn't like the query builder.

Another thing was that I wanted to have control over the JDBC Wrapper to have a simple way of implementing an auto-reconnect function, if it gets disconnected for some reason.

I finally decided to make an own ORM that fits my needs and here it is.

## Documentation
You can find the current docs on our [website](https://docs.javawebstack.org/framework/orm). This is a work-in-progress project though so it's not yet complete.

### Example usage

```java
class MyApp extends WebApplication {
    /* ... */
    protected void setupModules() {

        OAuth2Module oAuth2Module = new OAuth2Module();
        oAuth2Module
                .addService(new GithubOAuth2Service("", "", /*Redirect Host*/ "http://localhost:2222"))
                .setOAuth2Callback((service, exchange, callback, token, httpClient) -> {
                    return "Hello "+callback.getProfile().name;
                });
        addModule(oAuth2Module);
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
#### or Jitpack
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.JavaWebStack</groupId>
    <artifactId>Passport</artifactId>
    <version>COMMIT_HASH</version>
</dependency>
```

# Services
Service|Class|Control-Panel|More Information
---|---|---|---
Github|GithubOAuth2Service|[Github Developer Center](https://github.com/settings/developers)|-
Google|GoogleOAuth2Service|[Google Developer Console](https://console.developers.google.com/)|-
Discord|DiscordOAuth2Service|[Discord Developer Portal](https://discord.com/developers/applications)|-
Facebook|FacebookOAuth2Service|[Facebook Developer Center](https://console.developers.google.com/)|TODO
InteraApps|InteraAppsOAuth2Service|[IA-Accounts Developer Center](https://accounts.interaapps.de/developers/projects)|-