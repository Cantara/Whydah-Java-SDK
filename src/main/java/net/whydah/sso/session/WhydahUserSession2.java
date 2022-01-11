package net.whydah.sso.session;

import net.whydah.sso.commands.userauth.CommandValidateUserTokenId;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.user.helpers.UserTokenXpathHelper;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.WhydahUtil2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class WhydahUserSession2 {

    private static final Logger log = LoggerFactory.getLogger(WhydahUserSession2.class);
    private static final int SESSION_CHECK_INTERVAL = 60;

    private final WhydahApplicationSession2 was;
    private final UserCredential userCredential;

    private final AtomicReference<String> userTokenXMLRef = new AtomicReference<>();
    private final AtomicReference<String> userTokenIdRef = new AtomicReference<>();

    private final ScheduledExecutorService scheduler;

    private WhydahUserSession2() {
        this.was = null;
        this.userCredential = null;
        this.scheduler = null;
    }

    public WhydahUserSession2(WhydahApplicationSession2 was, UserCredential userCredential) {
        if (was == null || !ApplicationTokenID.isValid(was.getActiveApplicationTokenId())) {
            throw new IllegalArgumentException("Error, unable to initialize new user session, application session invalid: " + (was == null ? "(was == null)" : was.getActiveApplicationTokenId()));
        }

        this.was = was;
        this.userCredential = userCredential;

        initializeUserSession();
        this.scheduler = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> sf = scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        renewWhydahUserSession();
                    }
                },
                1, SESSION_CHECK_INTERVAL, TimeUnit.SECONDS);
    }

    public static Integer calculateTokenRemainingLifetimeInSeconds(String userTokenXml) {
        Long tokenLifespanMs = UserTokenXpathHelper.getLifespan(userTokenXml);
        Long tokenTimestampMsSinceEpoch = UserTokenXpathHelper.getTimestamp(userTokenXml);

        if (tokenLifespanMs == null || tokenTimestampMsSinceEpoch == null) {
            return null;
        }

        long endOfTokenLifeMs = tokenTimestampMsSinceEpoch + tokenLifespanMs;
        long remainingLifeMs = endOfTokenLifeMs - System.currentTimeMillis();
        return (int) (remainingLifeMs / 1000);
    }

    public static boolean expiresBeforeNextSchedule(Long timestamp) {

        long i = System.currentTimeMillis();
        long j = timestamp;
        long diffSeconds = j - i;
        if (diffSeconds < SESSION_CHECK_INTERVAL) {
            return true;
        }
        return false;
    }

    public String getActiveUserTokenId() {
        return userTokenIdRef.get();
    }

    public String getActiveUserToken() {
        return userTokenXMLRef.get();
    }

    public boolean hasRole(String roleName) {
        return UserXpathHelper.hasRoleFromUserToken(userTokenXMLRef.get(), was.getActiveApplicationTokenId(), roleName);
    }

    public boolean hasUASAccessAdminRole() {
        return WhydahUtil2.hasUASAccessAdminRole(userTokenXMLRef.get());
    }


    /*
     * @return true is session is active and working
     */
    public boolean hasActiveSession() {
        if (userTokenXMLRef.get() == null || userTokenXMLRef.get().length() < 4) {
            return false;
        }
        try {
            URI stsURI = new URI(was.getSTS());
            return new CommandValidateUserTokenId(stsURI, was.getActiveApplicationTokenId(), getActiveUserTokenId()).execute();
        } catch (Exception e) {
            return false;
        }
    }


    private void renewWhydahUserSession() {
        log.info("Renew user session");
        String userTokenXML = WhydahUtil2.logOnUser(was, userCredential);
        userTokenXMLRef.set(userTokenXML);
        if (!hasActiveSession()) {
            log.error("Error, unable to initialize new user session, userTokenXML:" + userTokenXML);
            for (int n = 0; n < 7; n++) {
                log.warn("Retrying renewing user session");
                if (n > 0) {
                    try {
                        Thread.sleep(1000 * n);
                    } catch (InterruptedException ie) {
                    }
                }
                userTokenXML = WhydahUtil2.logOnUser(was, userCredential);
                userTokenXMLRef.set(userTokenXML);
                if (userTokenXML != null && userTokenXML.length() >= 4) {
                    return; // got valid userTokenXML - no need to re-check validity remotely
                }
                // If we keep failing, let us force renew of application session too
                if (n > 3) {
                    was.resetApplicationSession();
                    n = 0; // TODO potential for-ever loop ?
                }
            }

        } else {
            log.info("Renew user session successfull.  userTokenXml:" + userTokenXML);
            Long expires = UserXpathHelper.getTimestampFromUserTokenXml(userTokenXML) + UserXpathHelper.getLifespanFromUserTokenXml(userTokenXML);
            if (expiresBeforeNextSchedule(expires)) {
                this.userTokenXMLRef.set(WhydahUtil2.extendUserSession(was, userCredential));
                userTokenIdRef.set(UserXpathHelper.getUserTokenId(userTokenXML));
            }
        }

    }

    private void initializeUserSession() {
        log.info("Initializing new user session");
        String userTokenXML = WhydahUtil2.logOnUser(was, userCredential);
        userTokenXMLRef.set(userTokenXML);
        if (userTokenXML == null || userTokenXML.length() < 4) {
            log.error("Error, unable to initialize new user session, userTokenXML:" + userTokenXML);
        } else {
            log.info("Initializing user session successfull.  userTokenXml:" + userTokenXML);
            userTokenIdRef.set(UserXpathHelper.getUserTokenId(userTokenXML));
        }
    }
}

