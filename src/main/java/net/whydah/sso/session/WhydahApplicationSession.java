package net.whydah.sso.session;

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
    private static WhydahApplicationSession instance = null;
    private String sts;
    private ApplicationCredential myAppCredential;
    private ApplicationToken applicationToken;


    protected WhydahApplicationSession() {
        this("https://whydahdev.cantara.no/tokenservice/", "99", "TestApp", "33879936R6Jr47D4Hj5R6p9qT");

    }

    protected WhydahApplicationSession(String sts, ApplicationCredential appCred) {
        this(sts, appCred.getApplicationID(), appCred.getApplicationName(), appCred.getApplicationSecret());
    }

    protected WhydahApplicationSession(String sts, String appId, String appName, String appSecret) {
        this.sts = sts;
        this.myAppCredential = new ApplicationCredential(appId, appName, appSecret);
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

    public static WhydahApplicationSession getInstance() {
        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
            synchronized (WhydahApplicationSession.class) {
                if (instance == null) {
                    instance = new WhydahApplicationSession();
                }
            }
        }
        return instance;
    }

    public static WhydahApplicationSession getInstance(String sts, ApplicationCredential appCred) {
        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
            synchronized (WhydahApplicationSession.class) {
                if (instance == null) {
                    instance = new WhydahApplicationSession(sts, appCred.getApplicationID(), appCred.getApplicationName(), appCred.getApplicationSecret());
                }
            }
        }
        return instance;
    }

    public static WhydahApplicationSession getInstance(String sts, String appId, String appName, String appSecret) {
        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
            synchronized (WhydahApplicationSession.class) {
                if (instance == null) {
                    instance = new WhydahApplicationSession(sts, appId, appName, appSecret);
                }
            }
        }
        return instance;
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
        if (applicationToken == null) {
            initializeWhydahApplicationSession();
        }
        return applicationToken;
    }

    public String getActiveApplicationTokenId() {
        if (applicationToken == null) {
            initializeWhydahApplicationSession();
        }
        return applicationToken.getApplicationTokenId();
    }

    public String getActiveApplicationName() {
        if (applicationToken == null) {
            initializeWhydahApplicationSession();
        }
        return applicationToken.getApplicationName();
    }

    public String getActiveApplicationTokenXML() {
        if (applicationToken == null) {
            initializeWhydahApplicationSession();
        }
        return ApplicationTokenMapper.toXML(applicationToken);
    }

    public String getSTS() {
        return sts;
    }

    public void killApplicationSession() {
        applicationToken = null;
        initializeWhydahApplicationSession();
    }


    private void renewWhydahApplicationSession() {
        if (applicationToken == null) {
            initializeWhydahApplicationSession();
        }
        if (!checkActiveSession()) {
            log.info("No active application session for applicationTokenId: {} getApplicationID: {}", applicationToken.getApplicationID(), applicationToken.getApplicationID());
            for (int n = 0; n < 3 || !checkActiveSession(); n++) {
                if (initializeWhydahApplicationSession()) {
                    break;
                }
                log.info("Unsuccessful attempt to logon application session, returned applicationtoken: " + getActiveApplicationTokenXML());
                try {
                    Thread.sleep(1000 * n);
                } catch (InterruptedException ie) {
                }
            }
        } else {
            log.info("Active application session found, applicationTokenId: {}  applicationID: {}  expires: {}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());
            ;
            Long expires = Long.parseLong(applicationToken.getExpires());
            if (expiresBeforeNextSchedule(expires)) {
                log.info("Active session expires before next check, re-new");
                for (int n = 0; n < 5; n++) {
                    String applicationTokenXML = WhydahUtil.extendApplicationSession(sts, getActiveApplicationTokenId(), 2000 + n * 1000);  // Wait a bit longer on retries
                    if (applicationTokenXML != null && applicationTokenXML.length() > 10) {
                        applicationToken = ApplicationTokenMapper.fromXml(applicationTokenXML);
                        if (checkActiveSession()) {
                            log.info("Success in renew applicationsession, applicationTokenId:" + applicationToken.getApplicationTokenId());
                            break;
                        }
                    } else {
                        log.info("Failed to renew applicationsession, returned response from STS: {}", applicationTokenXML);
                        if (n > 2) {
                            // OK, we wont get a renewed session, so we start a new one
                            if (initializeWhydahApplicationSession()) {
                                break;
                            }
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
        log.info("Initializing new application session with applicationID: {}", myAppCredential.getApplicationID());
        String applicationTokenXML = WhydahUtil.logOnApplication(sts, myAppCredential);
        if (!checkApplicationToken(applicationTokenXML)) {
            log.info("Error, unable to initialize new application session, applicationTokenXml:" + applicationTokenXML);
            return false;
        }
        setApplicationSessionParameters(applicationTokenXML);
        log.debug("Initializing new application session, applicationTokenId:" + applicationToken.getApplicationTokenId());
        return true;
    }

    private void setApplicationSessionParameters(String applicationTokenXML) {
        applicationToken = ApplicationTokenMapper.fromXml(applicationTokenXML);
        log.debug("New application session created for applicationID: {}, expires: {}", applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());
    }

    /**
     * @return true is session is active and working
     */
    public boolean checkActiveSession() {
        if (applicationToken == null || getActiveApplicationTokenId() == null || getActiveApplicationTokenId().length() < 4) {
            return false;
        }

        return new CommandValidateApplicationTokenId(getSTS(), getActiveApplicationTokenId()).execute();
    }

    /**
     * @return true if applicationTokenXML seems sensible
     */
    public boolean checkApplicationToken(String applicationTokenXML) {
        try {
            ApplicationToken at = ApplicationTokenMapper.fromXml(applicationTokenXML);
            if (at.getApplicationTokenId().length() > 8) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }


}
