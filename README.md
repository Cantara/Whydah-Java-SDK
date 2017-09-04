Whydah-Java-SDK
===============

![Build Status](https://jenkins.capraconsulting.no/buildStatus/icon?job=Whydah-Java-SDK) - [![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](http://www.repostatus.org/badges/latest/active.svg)](http://www.repostatus.org/#active) 

[![Known Vulnerabilities](https://snyk.io/test/github/Cantara/Whydah-Java-SDK/badge.svg)](https://snyk.io/test/github/Cantara/Whydah-Java-SDK)


A client library which aimed to make Whydah integration more easy and more resilient

 * XML and JSON parsing of Whydah datastructures sent over the wire.  (Whydah Typelib)
 * Util library for all the frequent used standard API calls
 * SessionHandler for ApplicationSessions and User Sessions
 * Client logic for using Whydah Web SSO - SSOLoginWebapp (SSOLWA) and STS (SecurityTokenService).

The 3rd party Admin SDK is found at [https://github.com/Cantara/Whydah-Admin-SDK](https://github.com/Cantara/Whydah-Admin-SDK)

For code and examples for other languages, see <https://github.com/cantara/Whydah-TestWebApp>


## Example code

```java
        // Log on application and user
        String userToken = WhydahUtil.logOnApplicationAndUser("https://whydahdev.cantara.no/tokenservice/",\\
                           new ApplicationCredential("applicationID","applicationname","applicationSecret"),\\
                           new UserCredential( "username", "password");
        // Get the user sessionId (userTokenId)
        String userTokenId = UserXpathHelper.getUserTokenId(userToken);
```

## Example code, with automatic user and application session renewwal
```java
        WhydahApplicationSession wasSession = new WhydahApplicationSession(uTokenSUri, \\
                           new ApplicationCredential(appId, appName, appSecret));
        WhydahUserSession wusSession = new WhydahUserSession(wasSession,userCredential);
        if (wusSession.hasRole("WhydahAdmin"){
          // do admin privilege operation
        }
```

## Binaries

Binaries and dependency information for Maven, Ivy, Gradle and others can be found at [http://mvnrepo.cantara.no](http://mvnrepo.cantara.no/index.html#nexus-search;classname~Whydah).

Example for Maven:

```xml
        <dependency>
            <groupId>net.whydah.sso</groupId>
            <artifactId>Whydah-Java-SDK</artifactId>
            <version>x.y.z</version>
        </dependency>
```


