package net.whydah.sso.session;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.commands.appauth.CommandValidateApplicationTokenId;
import net.whydah.sso.util.WhydahUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WhydahApplicationSession {

    private static final Logger log = LoggerFactory.getLogger(WhydahApplicationSession.class);
    private static final int SESSION_CHECK_INTERVAL = 30;  // Check every 30 seconds to adapt quickly
    private String sts;
    private ApplicationCredential myAppCredential;
    private String applicationTokenId;
    private String applicationName;
    private String applicationTokenXML;
    private ApplicationToken applicationToken;


    public WhydahApplicationSession() {
        this("https://whydahdev.cantara.no/tokenservice/", "99", "TestApp", "33879936R6Jr47D4Hj5R6p9qT");

    }


    public WhydahApplicationSession(String sts, ApplicationCredential appCred) {
        this(sts, appCred.getApplicationID(), appCred.getApplicationName(), appCred.getApplicationSecret());
    }

    public WhydahApplicationSession(String sts, String appId, String appName, String appSecret) {
        this.sts = sts;
        myAppCredential = new ApplicationCredential(appId, appName, appSecret);
        initializeWhydahApplicationSession();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> sf = scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        renewWhydahApplicationSession();
                    }
                },
                1, SESSION_CHECK_INTERVAL, TimeUnit.SECONDS);
    }

    public static boolean expiresBeforeNextSchedule(Long timestamp) {

        long i = System.currentTimeMillis();
        long j = (timestamp);
        long diffSeconds = j - i;
        if (diffSeconds < SESSION_CHECK_INTERVAL) {
            log.debug("expiresBeforeNextSchedule - re-new application session..");
            return true;
        }
        return false;
    }

    public ApplicationToken getActiveApplicationToken() {
        return applicationToken;
    }

    public String getActiveApplicationTokenId() {
        return applicationTokenId;
    }

    public String getActiveApplicationName() {
        return applicationName;
    }

    public String getActiveApplicationTokenXML() {
        return ApplicationTokenMapper.toXML(applicationToken);
    }

    public String getSTS() {
        return sts;
    }

    public void killApplicationSession() {
        applicationTokenId = null;
        applicationTokenXML = null;
        initializeWhydahApplicationSession();
    }


    private void renewWhydahApplicationSession() {
        if (!hasActiveSession()) {
            log.info("No active application session, applicationTokenId:" + applicationTokenId);
            for (int n = 0; n < 3 || !hasActiveSession(); n++) {
                applicationTokenXML = WhydahUtil.logOnApplication(sts, myAppCredential);
                if (isActiveSession(applicationTokenXML)) {
                    setApplicationSessionParameters(applicationTokenXML);
                    log.info("Successful renew of applicationsession, applicationTokenId:" + applicationTokenId);
                    break;
                }
                log.info("Unsuccessful attempt to renew application session, returned applicationtoken: " + applicationTokenXML);
                log.debug("Retrying renewing application session");
                try {
                    Thread.sleep(1000 * n);
                } catch (InterruptedException ie) {
                }
            }
        } else {
            log.info("Active session found, applicationTokenId:" + applicationTokenId);
            Long expires = ApplicationXpathHelper.getExpiresFromAppTokenXml(applicationTokenXML);
            if (expiresBeforeNextSchedule(expires)) {
                log.info("Active session expires before next check, re-new");
                for (int n = 0; n < 5; n++) {
                    applicationTokenXML = WhydahUtil.extendApplicationSession(sts, getActiveApplicationTokenId());
                    if (applicationTokenXML != null && applicationTokenXML.length() > 10) {
                        applicationTokenId = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(applicationTokenXML);
                        if (hasActiveSession()) {
                            log.info("Success in renew applicationsession, applicationTokenId:" + applicationTokenId);
                            break;
                        }
                    } else {
                        log.info("Fail to renew applicationsession");
                        if (n > 3) {
                            // OK, we wont get a renewed session, so we start a new one
                            initializeWhydahApplicationSession();
                            n = 0;
                        }
                    }
                    try {
                        Thread.sleep(1000 * n);
                    } catch (InterruptedException ie) {
                    }
                }
            }
        }
    }


    private boolean initializeWhydahApplicationSession() {
        log.info("Initializing new application session");
        applicationTokenXML = WhydahUtil.logOnApplication(sts, myAppCredential);
        if (!hasActiveSession()) {
            log.info("Error, unable to initialize new application session, applicationTokenXml:" + applicationTokenXML);
            return false;
        } else {
            setApplicationSessionParameters(applicationTokenXML);
            log.debug("Initializing new application session, applicationTokenId:" + applicationTokenId);
            return true;
        }
    }

    private void setApplicationSessionParameters(String applicationTokenXML) {
        applicationToken = ApplicationTokenMapper.fromXml(applicationTokenXML);
        applicationTokenId = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(applicationTokenXML);
        applicationName = ApplicationXpathHelper.getAppNameFromAppTokenXml(applicationTokenXML);
    }

    /*
* @return true is session is active and working
 */
    public boolean hasActiveSession() {
        if (applicationTokenId == null || applicationTokenId.length() < 4) {
            return false;
        }

        return new CommandValidateApplicationTokenId(getSTS(), getActiveApplicationTokenId()).execute();
        //return true;
    }

    /*
* @return true if applicationTokenXML seems sensible
*/
    public boolean isActiveSession(String applicationTokenXML) {
        if (applicationTokenXML.length() > 8) {
            return true;
        }
        try {
            ApplicationToken at = ApplicationTokenMapper.fromXml(applicationTokenXML);
            if (at.getApplicationID().length() > 8) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

}
