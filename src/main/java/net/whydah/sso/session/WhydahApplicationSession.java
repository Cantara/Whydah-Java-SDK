package net.whydah.sso.session;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.util.WhydahUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WhydahApplicationSession {

    private static final Logger log = LoggerFactory.getLogger(WhydahApplicationSession.class);
    private String sts;
    private ApplicationCredential myAppCredential;
    private String applicationTokenId;
    private String applicationName;
    private String applicationTokenXML;


    public WhydahApplicationSession() {
        this("https://whydahdev.altrancloud.com/tokenservice/", "99", "TestApp", "33879936R6Jr47D4Hj5R6p9qT");

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
                1, 50, TimeUnit.SECONDS);
    }

    public static boolean expiresBeforeNextSchedule(Long timestamp) {

        long i = System.currentTimeMillis();
        long j = (timestamp);
        long diffSeconds = j - i;
        if (diffSeconds < 60) {
            log.debug("expiresBeforeNextSchedule - re-new application session..");
            return true;
        }
        return false;
    }

    public String getActiveApplicationTokenId() {
        return applicationTokenId;
    }

    public String getActiveApplicationToken() {
        return applicationTokenXML;
    }

    public String getSTS() {
        return sts;
    }

    /*
    * @return true is session is active and working
     */
    public boolean hasActiveSession() {
        if (applicationTokenId == null || applicationTokenId.length() < 4) {
            return false;
        }
        return true;
    }

    private void renewWhydahApplicationSession() {
        if (!hasActiveSession()) {
            log.info("No active application session, applicationTokenId:" + applicationTokenId);
            for (int n = 0; n < 7 || !hasActiveSession(); n++) {
                applicationTokenXML = WhydahUtil.logOnApplication(sts, myAppCredential);
                applicationTokenId = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(applicationTokenXML);
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
                applicationTokenXML = WhydahUtil.extendApplicationSession(sts, getActiveApplicationTokenId());
                applicationTokenId = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(applicationTokenXML);
                log.info("Success in renew applicationsession, applicationTokenId:" + applicationTokenId);
            }
        }


    }

    private void initializeWhydahApplicationSession() {
        log.info("Initializing new application session");
        applicationTokenXML = WhydahUtil.logOnApplication(sts, myAppCredential);
        applicationTokenId = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(applicationTokenXML);
        if (applicationTokenXML == null || applicationTokenXML.length() < 4) {
            log.info("Error, unable to initialize new application session, applicationTokenXml:" + applicationTokenXML);

        } else {
            log.debug("Initializing new application session, applicationTokenId:" + applicationTokenId);
//        applicationTokenXML = WhydahUtil.extendApplicationSession(sts, appId, appSecret);
            applicationTokenId = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(applicationTokenXML);
        }
    }
}
