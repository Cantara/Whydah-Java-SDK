package net.whydah.sso.session;

import net.whydah.sso.application.mappers.ApplicationMapper;
import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.Application;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.commands.adminapi.application.CommandListApplications;
import net.whydah.sso.commands.appauth.CommandValidateApplicationTokenId;
import net.whydah.sso.session.baseclasses.ApplicationModelUtil;
import net.whydah.sso.user.helpers.UserTokenXpathHelper;
import net.whydah.sso.util.WhydahUtil;
import net.whydah.sso.whydah.DEFCON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class WhydahApplicationSession {

    private static final Logger log = LoggerFactory.getLogger(WhydahApplicationSession.class);
    private static final int SESSION_CHECK_INTERVAL = 50;  // Check every 30 seconds to adapt quickly
    private List<Application> applications = new LinkedList<Application>();
    private static WhydahApplicationSession instance = null;
    private String sts;
    private String uas;
    private ApplicationCredential myAppCredential;
    private ApplicationToken applicationToken;
    private DEFCON defcon = DEFCON.DEFCON5;
    private boolean disableUpdateAppLink=false;


    protected WhydahApplicationSession() {
        this("https://whydahdev.cantara.no/tokenservice/", "99", "TestApp", "33879936R6Jr47D4Hj5R6p9qT");

    }

    protected WhydahApplicationSession(String sts, ApplicationCredential appCred) {
        this(sts, appCred.getApplicationID(), appCred.getApplicationName(), appCred.getApplicationSecret());
    }

    protected WhydahApplicationSession(String sts, String appId, String appName, String appSecret) {
        this(sts, null, appId, appName, appSecret);
    }

    protected WhydahApplicationSession(String sts, String uas, String appId, String appName, String appSecret) {
        log.info("WhydahApplicationSession initializing: sts:{},  uas:{}, appId:{}, appName:{}, appSecret:{}", sts, uas, appId, appName, appSecret);
        this.sts = sts;
        this.uas = uas;
        this.myAppCredential = new ApplicationCredential(appId, appName, appSecret);
        initializeWhydahApplicationSession();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> sf = scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                    	try{
                    		renewWhydahApplicationSession();
                    	}catch(Exception ex){
                    		ex.printStackTrace();
                    	}
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
        log.info("WhydahApplicationSession getInstance(String sts, ApplicationCredential appCred) called");
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
        log.info("WhydahApplicationSession getInstance(String sts, String appId, String appName, String appSecret) appCred called");
        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
            synchronized (WhydahApplicationSession.class) {
                if (instance == null) {
                    instance = new WhydahApplicationSession(sts, null, appId, appName, appSecret);
                }
            }
        }
        return instance;
    }
    
    public static WhydahApplicationSession getInstance(String sts, String uas, String appId, String appName, String appSecret) {
        log.info("WhydahApplicationSession getInstance(String sts, String uas, String appId, String appName, String appSecret) appCred called");
        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
            synchronized (WhydahApplicationSession.class) {
                if (instance == null) {
                    instance = new WhydahApplicationSession(sts, uas, appId, appName, appSecret);
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
            log.debug("expiresBeforeNextSchedule - re-new application session.. diffseconds: {}", diffSeconds);
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
            if (applicationToken == null) {
                log.warn("WAS: Unable to initialize new Application Session - no ApplicationToken returned");
                return "";
            }
        }
        return ApplicationTokenMapper.toXML(applicationToken);
    }

    public String getSTS() {
        return sts;
    }

    public DEFCON getDefcon() {
        return defcon;
    }

    public void setDefcon(DEFCON defcon) {
        this.defcon = defcon;
        DEFCONHandler.handleDefcon(defcon);

    }

    public void updateDefcon(String userTokenXml) {
        String tokendefcon = UserTokenXpathHelper.getDEFCONLevel(userTokenXml);
        if (DEFCON.DEFCON5.equals(tokendefcon)) {
            defcon = DEFCON.DEFCON5;
            DEFCONHandler.handleDefcon(defcon);
        }
        if (DEFCON.DEFCON4.equals(tokendefcon)) {
            log.warn("DEFCON lecel is now DEFCON4");
            defcon = DEFCON.DEFCON4;
            DEFCONHandler.handleDefcon(defcon);

        }
        if (DEFCON.DEFCON3.equals(tokendefcon)) {
            log.error("DEFCON lecel is now DEFCON3");
            defcon = DEFCON.DEFCON3;
            DEFCONHandler.handleDefcon(defcon);

        }
        if (DEFCON.DEFCON2.equals(tokendefcon)) {
            log.error("DEFCON lecel is now DEFCON2");
            defcon = DEFCON.DEFCON2;
            DEFCONHandler.handleDefcon(defcon);

        }
        if (DEFCON.DEFCON1.equals(tokendefcon)) {
            log.error("DEFCON lecel is now DEFCON1");
            defcon = DEFCON.DEFCON1;
            DEFCONHandler.handleDefcon(defcon);
        }

    }

    public void killApplicationSession() {
        applicationToken = null;
        initializeWhydahApplicationSession();
    }


    public void renewWhydahApplicationSession() {
        log.trace("Renew WAS: Renew application session called");
        if (applicationToken == null) {
            initializeWhydahApplicationSession();
            Runtime.getRuntime().removeShutdownHook(Thread.currentThread());

        }
        if (!checkActiveSession()) {
            if (applicationToken != null) {
                log.info("Renew WAS: No active application session for applicationTokenId: {}, getApplicationID: {},   expires: {}", applicationToken.getApplicationID(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());
            }
            for (int n = 0; n < 3 || !checkActiveSession(); n++) {
                if (initializeWhydahApplicationSession()) {
                    break;
                }
                log.info("Renew WAS: Unsuccessful attempt to logon application session, returned applicationtokenXML: {}: ", getActiveApplicationTokenXML());
                try {
                    Thread.sleep(1000 * n);
                } catch (InterruptedException ie) {
                }
            }
        } else {
            log.trace("Renew WAS: Active application session found, applicationTokenId: {},  applicationID: {},  expires: {}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());

            Long expires = Long.parseLong(applicationToken.getExpires());
            if (expiresBeforeNextSchedule(expires)) {
                log.info("Renew WAS: Active session expires before next check, re-new");
                for (int n = 0; n < 5; n++) {
                    String applicationTokenXML = WhydahUtil.extendApplicationSession(sts, getActiveApplicationTokenId(), 2000 + n * 1000);  // Wait a bit longer on retries
                    if (applicationTokenXML != null && applicationTokenXML.length() > 10) {
                        applicationToken = ApplicationTokenMapper.fromXml(applicationTokenXML);
                        if (checkActiveSession()) {
                            log.info("Renew WAS: Success in renew applicationsession, applicationTokenId: {} - for applicationID: {}, expires: {}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());
                            break;
                        }
                    } else {
                        log.info("Renew WAS:: Failed to renew applicationsession, attempt:{}, returned response from STS: {}", n, applicationTokenXML);
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
        if (uas != null && uas.length() > 8) {
            startThreadAndUpdateAppLinks();
        }
    }


    private boolean initializeWhydahApplicationSession() {
        log.info("Initializing new application session with applicationID: {}", myAppCredential.getApplicationID());
        String applicationTokenXML = WhydahUtil.logOnApplication(sts, myAppCredential);
        if (!checkApplicationToken(applicationTokenXML)) {
            log.warn("InitWAS: Error, unable to initialize new application session, applicationTokenXml:" + applicationTokenXML);
            removeApplicationSessionParameters(myAppCredential.getApplicationID());
            return false;
        }
        setApplicationSessionParameters(applicationTokenXML);
        log.info("InitWAS:: Initialized new application session, applicationTokenId:{}, applicationID: {}, expires: {}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());
        return true;
    }

    private void setApplicationSessionParameters(String applicationTokenXML) {
        applicationToken = ApplicationTokenMapper.fromXml(applicationTokenXML);
        log.info("WAS: New application session created for applicationID: {}, expires: {}", applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());
    }

    private void removeApplicationSessionParameters(String applicationID) {
        applicationToken = null;
        log.info("WAS: Application session removed for application: {}");
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


    /**
     * Application cache section - keep a cache of configured applications
     */

    public List<Application> getApplicationList() {
        return applications;
    }

    private void setAppLinks(List<Application> newapplications) {
        applications = newapplications;
    }

    public void updateApplinks() {
        if (uas == null || uas.length() < 8) {
            log.warn("Calling updateAppLinks without was initialized");
            return;
        }
        URI userAdminServiceUri= URI.create(uas);

        if ((ApplicationModelUtil.shouldUpdate(5) || getApplicationList() == null || getApplicationList().size() < 2) && applicationToken != null) {
            String applicationsJson = new CommandListApplications(userAdminServiceUri,  applicationToken.getApplicationTokenId()).execute();
            log.debug("WAS: updateApplinks: AppLications returned:" + applicationsJson);
            if (applicationsJson != null) {
                if (applicationsJson.length() > 20) {
                    setAppLinks(ApplicationMapper.fromJsonList(applicationsJson));
                }
            }
        }
    }

    public void updateApplinks(boolean forceUpdate) {
    	if(disableUpdateAppLink){
    		return;
    	}
        if (uas == null || uas.length() < 8) {
            log.warn("Calling updateAppLinks without was initialized");
            return;
        }
        URI userAdminServiceUri= URI.create(uas);

        if (forceUpdate && applicationToken != null) {
            String applicationsJson = new CommandListApplications(userAdminServiceUri,  applicationToken.getApplicationTokenId()).execute();
            log.debug("WAS: updateApplinks: AppLications returned:" + applicationsJson);
            if (applicationsJson != null) {
                if (applicationsJson.length() > 20) {
                    setAppLinks(ApplicationMapper.fromJsonList(applicationsJson));
                }
            }
        }
    }
    
    private void startThreadAndUpdateAppLinks() {
    	if(disableUpdateAppLink){
    		return;
    	}
        if (uas == null || uas.length() < 8) {
            log.info("Started WAS without UAS configuration, wont keep an updated applicationlist");
            return;
        } else {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                public void run() {
                    updateApplinks();
                    log.debug("WAS: Asynchronous startThreadAndUpdateAppLinks task executed");
                }
            });
            executorService.shutdown();
        }
    }

	public boolean isDisableUpdateAppLink() {
		return disableUpdateAppLink;
	}

	public void setDisableUpdateAppLink(boolean disableUpdateAppLink) {
		this.disableUpdateAppLink = disableUpdateAppLink;
	}

}
