Whydah-Java-SDK
===============

![Build Status](http://jenkins.capraconsulting.no/buildStatus/icon?job=Whydah-Java-SDK)


A client library which aimed to make Whydah integration more easy and more resilient

 * XML and JSON parsing of Whydah datastructures sent over the wire.
 * Util library for all the frequent used API calls
 * SessionHandler for ApplicationSessions and User Sessions
 * Client logic for using Whydah Web SSO - SSOLoginWebapp (SSOLWA).
   * The Java SDK is in a really early stage, and is currently used to experiment with a new remoting approach to increase system resilliance
* Client logic for using administrative API in UserAdminService (UAS) for applications with appropriate rights
    * Used as a TEST driver for the new UAS admin API's

For code and examples for other languages, see <https://github.com/cantara/Whydah-TestWebApp>


## Example code

```java
        // Log on application and user
        String userToken = WhydahUtil.logOnApplicationAndUser("https://whydahdev.cantara.no/tokenservice/",\\
                           "applicationID","applicationSecret", "username", "password");
        // Log get the user sessionId (userTokenId)
        String userTokenId = UserXpathHelper.getUserTokenId(userToken);
```

## Example code, with automatic session renewwal
```java
        WhydahApplicationSession aSession = new WhydahApplicationSssion(uTokenSUri, appId, appSecret);
        WhydahUserSession uSession = new WhydahUserSession(aSession,userCredential);
        if (uSession.hasRole("WhydahAdmin"){
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


