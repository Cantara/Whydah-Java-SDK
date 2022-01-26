package net.whydah.sso.session.experimental;

import net.whydah.sso.application.types.Application;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.session.ThreatSignalBuilder;
import net.whydah.sso.user.types.UserToken;
import net.whydah.sso.whydah.DEFCON;
import net.whydah.sso.whydah.ThreatSignal;

import java.util.List;
import java.util.function.Consumer;

/**
 * Experimental interface subject to change at any time. Only use this if you are prepared to adapt to any API changes
 * that might come. This comment will be removed when the interface is stable and no longer experimental. Even the class
 * name and location could and probably will change.
 */
public interface WhydahApplicationSession3 extends AutoCloseable {

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
    boolean hasActiveSession();

    /**
     * @return true if applicationTokenXML seems sensible
     */
    boolean checkApplicationToken(String applicationTokenXML);

    void reportThreatSignal(ThreatSignal threatSignal);

    default void reportThreatSignal(Consumer<ThreatSignalBuilder> consumer) {
        ThreatSignalBuilder builder = threatSignalBuilder();
        consumer.accept(builder);
        ThreatSignal threatSignal = builder.build();
        reportThreatSignal(threatSignal);
    }

    List<Application> getApplicationList();

    void updateApplinks();

    boolean hasApplicationMetaData();

    void updateApplinks(boolean forceUpdate);

    boolean isDisableUpdateAppLink();

    void setDisableUpdateAppLink(boolean disableUpdateAppLink);

    boolean isWhiteListed(String suspect);

    ThreatSignalBuilder threatSignalBuilder();

    @Override
    void close();
}


