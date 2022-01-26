package net.whydah.sso.session;

import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.whydah.ThreatSignal;

public interface WhydahApplicationSession2 extends WhydahApplicationSession {

    static WhydahApplicationSession2 getInstance(String sts, ApplicationCredential appCred) {
        return DefaultWhydahApplicationSessionSingleton.getInstance(sts, appCred);
    }

    static WhydahApplicationSession2 getInstance(String sts, String uas, ApplicationCredential appCred) {
        return DefaultWhydahApplicationSessionSingleton.getInstance(sts, uas, appCred);
    }

    static boolean expiresBeforeNextSchedule(Long timestamp) {
        return DefaultWhydahApplicationSessionSingleton.getInstance().expiresBeforeNextScheduledSessionCheck(timestamp);
    }

    static ThreatSignal createThreat(String clientIpAddress, String source, String text, Object[] additionalProperties, ThreatSignal.SeverityLevel severity, boolean isImmediateThreat) {
        return DefaultWhydahApplicationSessionSingleton.getInstance().threatSignalBuilder()
                .withClientIpAddress(clientIpAddress)
                .withSource(source)
                .withText(text)
                .withAdditionalProperties(additionalProperties)
                .withSeverity(severity)
                .withImmediateThreat(isImmediateThreat)
                .build();
    }

    static ThreatSignal createThreat(String clientIpAddress, String source, String text) {
        return DefaultWhydahApplicationSessionSingleton.getInstance().threatSignalBuilder()
                .withClientIpAddress(clientIpAddress)
                .withSource(source)
                .withText(text)
                .build();
    }

    static ThreatSignal createThreat(String clientIpAddress, String source, String text, Object[] details) {
        return DefaultWhydahApplicationSessionSingleton.getInstance().threatSignalBuilder()
                .withClientIpAddress(clientIpAddress)
                .withSource(source)
                .withText(text)
                .withAdditionalProperties(details)
                .build();
    }

    static ThreatSignal createThreat(String clientIpAddress, String source, String text, Object[] details, ThreatSignal.SeverityLevel severity) {
        return DefaultWhydahApplicationSessionSingleton.getInstance().threatSignalBuilder()
                .withClientIpAddress(clientIpAddress)
                .withSource(source)
                .withText(text)
                .withAdditionalProperties(details)
                .withSeverity(severity)
                .build();
    }

    static ThreatSignal createThreat(String text) {
        return DefaultWhydahApplicationSessionSingleton.getInstance().threatSignalBuilder()
                .withText(text)
                .build();
    }

    static ThreatSignal createThreat(String text, Object[] details) {
        return DefaultWhydahApplicationSessionSingleton.getInstance().threatSignalBuilder()
                .withText(text)
                .withAdditionalProperties(details)
                .build();
    }
}


