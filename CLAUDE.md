# Whydah-Java-SDK

## Purpose
Core client library for integrating Java applications with the Whydah IAM/SSO system. Provides XML/JSON parsing of Whydah data structures, utility methods for standard API calls, session management, and client logic for SSOLoginWebApp and SecurityTokenService.

## Tech Stack
- Language: Java 21
- Framework: None (pure library)
- Build: Maven
- Key dependencies: Whydah-TypeLib, Hystrix, SLF4J

## Architecture
Foundational SDK library that all Whydah-integrated Java applications depend on. Provides `WhydahApplicationSession` and `WhydahUserSession` for automatic session renewal, `WhydahUtil` for simplified login flows, and data structure helpers from Whydah-TypeLib. The Admin SDK extends this with administrative capabilities.

## Key Entry Points
- `WhydahUtil` - Simplified login helper
- `WhydahApplicationSession` - Application session with auto-renewal
- `WhydahUserSession` - User session with role checking
- `pom.xml` - Maven coordinates: `net.whydah.sso:Whydah-Java-SDK`

## Development
```bash
# Build
mvn clean install

# Test
mvn test
```

## Domain Context
Core integration library for the Whydah IAM ecosystem. Every Java application that authenticates users or manages sessions through Whydah depends on this SDK. Foundation for Whydah-Admin-SDK, used by all Whydah services and client applications.
