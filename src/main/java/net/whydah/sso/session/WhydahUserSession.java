package net.whydah.sso.session;

import net.whydah.sso.commands.userauth.CommandValidateUserTokenId;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.user.helpers.UserTokenXpathHelper;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.WhydahUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class WhydahUserSession {


    private static final Logger log = LoggerFactory.getLogger(WhydahUserSession.class);
    private static final int SESSION_CHECK_INTERVAL = 60;

    private final AtomicReference<WhydahApplicationSession> was = new AtomicReference<>();
    private final AtomicReference<UserCredential> userCredential = new AtomicReference<>();
    private final AtomicReference<String> userTokenId = new AtomicReference<>();
    private final AtomicReference<String> userTokenXML = new AtomicReference<>();

    private WhydahUserSession() {

    }


    public WhydahUserSession(WhydahApplicationSession was, UserCredential userCredential) {
        if (was == null || !ApplicationTokenID.isValid(was.getActiveApplicationTokenId())) {
            log.error("Error, unable to initialize new user session, application session invalid: " + was.getActiveApplicationTokenId());
            return;
        }

        this.was.set(was);
        this.userCredential.set(userCredential);
        initializeUserSession();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
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
        return userTokenId.get();
    }

    public String getActiveUserToken() {
        return userTokenXML.get();
    }

    public boolean hasRole(String roleName) {
        return UserXpathHelper.hasRoleFromUserToken(userTokenXML.get(), was.get().getActiveApplicationTokenId(), roleName);
    }

    public boolean hasUASAccessAdminRole() {
        return WhydahUtil.hasUASAccessAdminRole(userTokenXML.get());
    }


    /*
    * @return true is session is active and working
     */
    public boolean hasActiveSession() {
        if (userTokenXML.get() == null || userTokenXML.get().length() < 4) {
            return false;
        }
        try {
            URI stsURI = new URI(was.get().getSTS());
            return new CommandValidateUserTokenId(stsURI, was.get().getActiveApplicationTokenId(), getActiveUserTokenId()).execute();
        } catch (Exception e) {
            return false;
        }
    }


    private void renewWhydahUserSession() {
        log.info("Renew user session");
        userTokenXML.set(WhydahUtil.logOnUser(was.get(), userCredential.get()));
        if (!hasActiveSession()) {
            log.error("Error, unable to initialize new user session, userTokenXML:" + userTokenXML.get());
            for (int n = 0; n < 7 || hasActiveSession(); n++) {
                userTokenXML.set(WhydahUtil.logOnUser(was.get(), userCredential.get()));
                log.warn("Retrying renewing user session");
                try {
                    Thread.sleep(1000 * n);
                } catch (InterruptedException ie) {
                }
                // If we keep failing, let us force renew of application session too
                if (n > 3) {
                    was.get().resetApplicationSession();
                    n = 0;
                }
            }

        } else {
            log.info("Renew user session successfull.  userTokenXml:" + userTokenXML.get());
            Long expires = UserXpathHelper.getTimestampFromUserTokenXml(userTokenXML.get()) + UserXpathHelper.getLifespanFromUserTokenXml(userTokenXML.get());
            if (expiresBeforeNextSchedule(expires)) {
                this.userTokenXML.set(WhydahUtil.extendUserSession(was.get(), userCredential.get()));
                userTokenId.set(UserXpathHelper.getUserTokenId(this.userTokenXML.get()));
            }
        }

    }

    private void initializeUserSession() {
        log.info("Initializing new user session");
        userTokenXML.set(WhydahUtil.logOnUser(was.get(), userCredential.get()));
        if (userTokenXML.get() == null || userTokenXML.get().length() < 4) {
            log.error("Error, unable to initialize new user session, userTokenXML:" + userTokenXML.get());
        } else {
            log.info("Initializing user session successfull.  userTokenXml:" + userTokenXML.get());
            userTokenId.set(UserXpathHelper.getUserTokenId(this.userTokenXML.get()));
        }
    }
}


