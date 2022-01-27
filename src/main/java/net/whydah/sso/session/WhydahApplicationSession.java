package net.whydah.sso.session;

import net.whydah.sso.application.types.Application;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.user.types.UserToken;
import net.whydah.sso.whydah.DEFCON;
import net.whydah.sso.whydah.ThreatSignal;
import net.whydah.sso.whydah.ThreatSignal.SeverityLevel;

import java.util.List;

public interface WhydahApplicationSession extends AutoCloseable {

    static WhydahApplicationSession getInstance(String sts, ApplicationCredential appCred) {
        return DefaultWhydahApplicationSessionSingleton.getInstance(sts, appCred);
    }

    static WhydahApplicationSession getInstance(String sts, String uas, ApplicationCredential appCred) {
        return DefaultWhydahApplicationSessionSingleton.getInstance(sts, uas, appCred);
    }

    static boolean expiresBeforeNextSchedule(Long timestamp) {
        return DefaultWhydahApplicationSessionSingleton.getInstance().expiresBeforeNextScheduledSessionCheck(timestamp);
    }

    static ThreatSignal createThreat(String clientIpAddress, String source, String text, Object[] additionalProperties, SeverityLevel severity, boolean isImmediateThreat) {
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

    static ThreatSignal createThreat(String clientIpAddress, String source, String text, Object[] details, SeverityLevel severity) {
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

    boolean expiresBeforeNextScheduledSessionCheck(Long timestamp);

    ApplicationCredential getMyApplicationCredential();

    ApplicationToken getActiveApplicationToken();

    String getActiveApplicationTokenId();

    String getActiveApplicationName();

    String getActiveApplicationTokenXML();

    String getSTS();

    String getUAS();

    DEFCON getDefcon();

    void setDefcon(DEFCON defcon);

    boolean hasUASAccessAdminRole(UserToken userToken);

    void updateDefcon(String userTokenXml);

    void resetApplicationSession();

    void setApplicationToken(ApplicationToken myApplicationToken);

    /**
     * @return true is session is active and working
     */
    boolean checkActiveSession();

    /**
     * @return true is session is active and working
     */
    boolean hasActiveSession();

    /**
     * @return true if applicationTokenXML seems sensible
     */
    boolean checkApplicationToken(String applicationTokenXML);

    default void reportThreatSignal(String threatMessage) {
        reportThreatSignal(((DefaultWhydahApplicationSession) this).threatSignalBuilder()
                .withText(threatMessage)
                .build());
    }

    default void reportThreatSignal(String threatMessage, Object[] details) {
        reportThreatSignal(((DefaultWhydahApplicationSession) this).threatSignalBuilder()
                .withText(threatMessage)
                .withAdditionalProperties(details)
                .build());
    }

    default void reportThreatSignal(String clientIpAddress, String source, String threatMessage) {
        reportThreatSignal(((DefaultWhydahApplicationSession) this).threatSignalBuilder()
                .withClientIpAddress(clientIpAddress)
                .withSource(source)
                .withText(threatMessage)
                .build());
    }

    default void reportThreatSignal(String clientIpAddress, String source, String threatMessage, Object[] details) {
        reportThreatSignal(((DefaultWhydahApplicationSession) this).threatSignalBuilder()
                .withClientIpAddress(clientIpAddress)
                .withSource(source)
                .withText(threatMessage)
                .withAdditionalProperties(details)
                .build());
    }

    default void reportThreatSignal(String clientIpAddress, String source, String threatMessage, Object[] details, SeverityLevel severity) {
        reportThreatSignal(((DefaultWhydahApplicationSession) this).threatSignalBuilder()
                .withClientIpAddress(clientIpAddress)
                .withSource(source)
                .withText(threatMessage)
                .withAdditionalProperties(details)
                .withSeverity(severity)
                .build());
    }

    void reportThreatSignal(ThreatSignal threatSignal);

    List<Application> getApplicationList();

    void updateApplinks();

    boolean hasApplicationMetaData();

    void updateApplinks(boolean forceUpdate);

    boolean isDisableUpdateAppLink();

    void setDisableUpdateAppLink(boolean disableUpdateAppLink);

    boolean isWhiteListed(String suspect);

    @Override
    void close();
}


