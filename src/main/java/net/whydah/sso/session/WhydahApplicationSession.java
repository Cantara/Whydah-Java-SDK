package net.whydah.sso.session;

import net.whydah.sso.application.mappers.ApplicationMapper;
import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.Application;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.commands.appauth.CommandGetApplicationKey;
import net.whydah.sso.commands.appauth.CommandValidateApplicationTokenId;
import net.whydah.sso.commands.application.CommandListApplications;
import net.whydah.sso.commands.threat.CommandSendThreatSignal;
import net.whydah.sso.commands.threat.ThreatDefManyLoginAttemptsFromSameIPAddress;
import net.whydah.sso.commands.threat.ThreatDefTooManyRequestsForOneEndpoint;
import net.whydah.sso.commands.threat.ThreatObserver;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.session.baseclasses.ApplicationModelUtil;
import net.whydah.sso.session.baseclasses.CryptoUtil;
import net.whydah.sso.session.baseclasses.ExchangeableKey;
import net.whydah.sso.user.helpers.UserTokenXpathHelper;
import net.whydah.sso.user.types.UserToken;
import net.whydah.sso.util.WhydahUtil;
import net.whydah.sso.whydah.DEFCON;
import net.whydah.sso.whydah.ThreatSignal;
import net.whydah.sso.whydah.ThreatSignal.SeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static net.whydah.sso.util.LoggerUtil.first50;

public class WhydahApplicationSession {

    private static final Logger log = LoggerFactory.getLogger(WhydahApplicationSession.class);
    public static final int APPLICATION_SESSION_CHECK_INTERVAL_IN_SECONDS = 10;  // Check every 30 seconds to adapt quickly
    private List<Application> applications = new LinkedList<Application>();
    private static volatile WhydahApplicationSession instance = null;
    private static final Object initialize_lock = new Object();
    private static final Object logon_lock = new Object();
    private String sts;
    private String uas;

    private static ApplicationCredential myAppCredential;
    private static int logonAttemptNo = 0;
    private static ApplicationToken applicationToken;
    private DEFCON defcon = DEFCON.DEFCON5;
    private boolean disableUpdateAppLink = false;

    private static final int[] FIBONACCI = new int[]{0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144};
    private static ScheduledExecutorService initialize_scheduler;
    private static ScheduledExecutorService renew_scheduler;

    private ThreatObserver threatObserver;
    private static boolean isInitialized = false;

    static {
        initialize_scheduler = Executors.newScheduledThreadPool(1);
        renew_scheduler = Executors.newScheduledThreadPool(1);
    }

    /**
     * Protected singleton constructors
     */
    protected WhydahApplicationSession(String sts, String uas, ApplicationCredential myAppCredential) {
        synchronized (WhydahApplicationSession.class) {
            log.info("WhydahApplicationSession initializing: sts:{},  uas:{}, myAppCredential:{}", sts, uas, myAppCredential);
            if (instance == null) {
                this.sts = sts;
                this.uas = uas;
                this.myAppCredential = myAppCredential;

                //register threat definitions here
                threatObserver = new ThreatObserver(this);
                getThreatObserver().registerDefinition(new ThreatDefManyLoginAttemptsFromSameIPAddress());
                getThreatObserver().registerDefinition(new ThreatDefTooManyRequestsForOneEndpoint());
                //register more if any

                initializeWhydahApplicationSession(true);
                ScheduledFuture<?> sf = renew_scheduler.scheduleAtFixedRate(
                        new Runnable() {
                            public void run() {
                                try {
                                    renewWhydahApplicationSession();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        },
                        5, APPLICATION_SESSION_CHECK_INTERVAL_IN_SECONDS, TimeUnit.SECONDS);
            }
        }
    }


    public static WhydahApplicationSession getInstance(String sts, ApplicationCredential appCred) {
        log.info("WhydahApplicationSession getInstance(String sts, ApplicationCredential appCred) called");
        if (instance == null) {
            synchronized (initialize_lock) {
                instance = new WhydahApplicationSession(sts, null, appCred);
            }
        }
        return instance;
    }

    public static WhydahApplicationSession getInstance(String sts, String uas, ApplicationCredential appCred) {
        log.info("WhydahApplicationSession getInstance(String sts, String uas, ApplicationCredential appCred) called");
        if (instance == null) {
            synchronized (initialize_lock) {
                instance = new WhydahApplicationSession(sts, uas, appCred);
            }
        }
        return instance;
    }


    public static ApplicationCredential getMyApplicationCredential() {
        return myAppCredential;
    }

    public static boolean expiresBeforeNextSchedule(Long timestamp) {

        long currentTime = System.currentTimeMillis();
        long expiresAt = (timestamp);
        long diffSeconds = (expiresAt - currentTime) / 1000;
        log.debug("expiresBeforeNextSchedule - expiresAt: {} - now: {} - expires in: {} seconds", expiresAt, currentTime, diffSeconds);
        if (diffSeconds < (APPLICATION_SESSION_CHECK_INTERVAL_IN_SECONDS * 3)) {
            log.debug("expiresBeforeNextSchedule - re-new application session.. diffseconds: {}", diffSeconds);
            return true;
        }
        return false;
    }

    public ApplicationToken getActiveApplicationToken() {
        if (applicationToken == null) {
//            initializeWhydahApplicationSession();
        }
        return applicationToken;
    }

    public String getActiveApplicationTokenId() {
        if (applicationToken == null) {
//            initializeWhydahApplicationSession();
        }
        if (applicationToken == null) {
            return "";
        }
        return applicationToken.getApplicationTokenId();
    }

    public String getActiveApplicationName() {
        if (applicationToken == null) {
//            initializeWhydahApplicationSession();
        }
        if (applicationToken == null) {
            return "N/A";
        }
        return applicationToken.getApplicationName();
    }

    public String getActiveApplicationTokenXML() {
        if (applicationToken == null) {
            //           initializeWhydahApplicationSession();
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

    public String getUAS() {
        return uas;
    }

    public DEFCON getDefcon() {
        return defcon;
    }

    public void setDefcon(DEFCON defcon) {
        this.defcon = defcon;
        DEFCONHandler.handleDefcon(defcon);

    }

    public boolean hasUASAccessAdminRole(UserToken userToken) {
        return WhydahUtil.hasUASAccessAdminRole(userToken);
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

    public synchronized void killApplicationSession() {
        setApplicationToken(null);
        initializeWhydahApplicationSession(false);
    }

    public static synchronized void setApplicationToken(ApplicationToken myApplicationToken) {
        applicationToken = myApplicationToken;
    }


    private void renewWhydahApplicationSession() {
        log.trace("Renew WAS: Renew application session called");
        if (applicationToken == null) {
            initializeWhydahApplicationSession(false);
            Runtime.getRuntime().removeShutdownHook(Thread.currentThread());

        }
        if (!checkActiveSession()) {
            if (applicationToken == null) {
                log.info("Renew WAS: No active application session, applicationToken:null, myAppCredential:{}, logonAttemptNo:{}", myAppCredential, logonAttemptNo);
            }
            initializeWhydahApplicationSession(false);
        } else {
            log.trace("Renew WAS: Active application session found, applicationTokenId: {},  applicationID: {},  expires: {}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());

            Long expires = Long.parseLong(applicationToken.getExpires());
            if (expiresBeforeNextSchedule(expires)) {
                log.info("Renew WAS: Active session expires before next check, re-new - applicationTokenId: {},  applicationID: {},  expires: {}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());
                for (int n = 0; n < 5; n++) {
                    String applicationTokenXML = WhydahUtil.extendApplicationSession(sts, getActiveApplicationTokenId(), 2000 + n * 1000);  // Wait a bit longer on retries
                    if (applicationTokenXML != null && applicationTokenXML.length() > 10) {
                        setApplicationToken(ApplicationTokenMapper.fromXml(applicationTokenXML));
                        if (checkActiveSession()) {
                            log.info("Renew WAS: Success in renew applicationsession, applicationTokenId: {} - for applicationID: {}, expires: {}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());
                            log.debug("Renew WAS: - expiresAt: {} - now: {} - expires in: {} seconds", applicationToken.getExpires(), System.currentTimeMillis(), (Long.parseLong(applicationToken.getExpires()) - System.currentTimeMillis()) / 1000);
                            String exchangeableKeyString = new CommandGetApplicationKey(URI.create(sts), applicationToken.getApplicationTokenId()).execute();
                            log.debug("Found exchangeableKeyString: {}", exchangeableKeyString);
                            ExchangeableKey exchangeableKey = new ExchangeableKey(exchangeableKeyString);
                            log.debug("Found exchangeableKey: {}", exchangeableKey);
                            try {
                                CryptoUtil.setExchangeableKey(exchangeableKey);

                            } catch (Exception e) {
                                log.warn("Unable to update CryptoUtil with new cryptokey", e);
                            }
                            break;
                        } else {
                            log.info("Renew WAS: received invaled applicationtoken in renew applicationsession, applicationTokenId: {} - for applicationID: {}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID());

                        }
                    } else {
                        log.info("Renew WAS: Failed to renew applicationsession, attempt:{}, returned response from STS: {}", n, applicationTokenXML);
                        if (n > 2) {
                            // OK, we wont get a renewed session, so we start a new one
                            initializeWhydahApplicationSession(false);
                            if (logonAttemptNo == 0) {
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

    private synchronized void initializeWhydahApplicationSession(boolean force) {
        if (instance == null && force == false) {
            return;  //  Block starting threads outside instance scope
        }
        if (isInitialized) {
            return;
        }
        logonAttemptNo = 1;
        String applicationTokenXML = WhydahUtil.logOnApplication(sts, myAppCredential);
        if (checkApplicationToken(applicationTokenXML)) {
            isInitialized = true;
            setApplicationSessionParameters(applicationTokenXML);
            log.info("InitWAS {}: Initialized new application session, applicationTokenId:{}, applicationID: {}, applicationName: {}, expires: {}", logonAttemptNo, applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getApplicationName(), applicationToken.getExpiresFormatted());
            logonAttemptNo = 0;
        } else {
            ScheduledFuture<?> sf = initialize_scheduler.schedule(
                    new Runnable() {
                        public void run() {
                            try {
                                initializeWhydahApplicationSessionThread();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }, 5, TimeUnit.SECONDS);
        }
    }

    private boolean initializeWhydahApplicationSessionThread() {
        log.info("Initializing new application session {} with applicationCredential: {}", logonAttemptNo, myAppCredential);

        try {
            Thread.sleep(FIBONACCI[logonAttemptNo] * 10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        synchronized (logon_lock) {
            if (isInitialized) {
                return true;
            }
            String applicationTokenXML = WhydahUtil.logOnApplication(sts, myAppCredential);
            if (!checkApplicationToken(applicationTokenXML)) {
                logonAttemptNo++;
                if (logonAttemptNo > 12) {
                    logonAttemptNo = 1;
                }
                log.warn("InitWAS {}: Error, unable to initialize new application session, applicationTokenXml: {}", logonAttemptNo, first50(applicationTokenXML));
                removeApplicationSessionParameters();
                isInitialized = false;
                return false;
            }
            isInitialized = true;
            setApplicationSessionParameters(applicationTokenXML);
        }
        log.info("InitWAS {}: Initialized new application session, applicationTokenId:{}, applicationID: {}, applicationName: {}, expires: {}", logonAttemptNo, applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getApplicationName(), applicationToken.getExpiresFormatted());
        logonAttemptNo = 0;
        return true;
    }

    private synchronized void setApplicationSessionParameters(String applicationTokenXML) {
        setApplicationToken(ApplicationTokenMapper.fromXml(applicationTokenXML));
        String exchangeableKeyString = new CommandGetApplicationKey(URI.create(sts), applicationToken.getApplicationTokenId()).execute();
        log.debug("Found exchangeableKeyString: {}", exchangeableKeyString);
        ExchangeableKey exchangeableKey = new ExchangeableKey(exchangeableKeyString);
        log.debug("Found exchangeableKey: {}", exchangeableKey);
        try {
            CryptoUtil.setExchangeableKey(exchangeableKey);

        } catch (Exception e) {
            log.warn("Unable to update CryptoUtil with new cryptokey", e);
        }
        log.info("WAS {}: New application session created for applicationID: {}, applicationTokenID: {}, expires: {}, key:{}", logonAttemptNo, applicationToken.getApplicationID(), applicationToken.getApplicationTokenId(), applicationToken.getExpiresFormatted(), CryptoUtil.getActiveKey());
    }

    private void removeApplicationSessionParameters() {
        setApplicationToken(null);
        isInitialized = false;
        log.info("WAS {}: Application session removed for applicationID: {} applicationName: {},", logonAttemptNo, myAppCredential.getApplicationID(), myAppCredential.getApplicationName());
    }

    /**
     * @return true is session is active and working
     */
    public boolean checkActiveSession() {
        if (applicationToken == null || !ApplicationTokenID.isValid(getActiveApplicationTokenId())) {
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
     *
     */
    public void reportThreatSignal(String threatMessage) {
        reportThreatSignal(createThreat(threatMessage));
    }

    public void reportThreatSignal(String threatMessage, Object[] details) {
        reportThreatSignal(createThreat(threatMessage, details));
    }

    public void reportThreatSignal(String clientIpAddress, String source, String threatMessage) {
        reportThreatSignal(createThreat(clientIpAddress, source, threatMessage));
    }

    public void reportThreatSignal(String clientIpAddress, String source, String threatMessage, Object[] details) {
        reportThreatSignal(createThreat(clientIpAddress, source, threatMessage, details));
    }

    public void reportThreatSignal(String clientIpAddress, String source, String threatMessage, Object[] details, SeverityLevel severity) {
        reportThreatSignal(createThreat(clientIpAddress, source, threatMessage, details, severity));
    }

    /**
     *
     */
    public void reportThreatSignal(ThreatSignal threatSignal) {
        try {
            new CommandSendThreatSignal(URI.create(getSTS()), getActiveApplicationTokenId(), threatSignal).queue();
        } catch (Exception e) {

        }
    }

    public static ThreatSignal createThreat(String clientIpAddress, String source, String text, Object[] additionalProperties, SeverityLevel severity, boolean isImmediateThreat) {
        ThreatSignal threatSignal = new ThreatSignal();
        if (instance != null) {
            threatSignal.setSignalEmitter(instance.getActiveApplicationName() + " [" + WhydahUtil.getMyIPAddresssesString() + "]");
            threatSignal.setAdditionalProperty("DEFCON", instance.getDefcon());
        }
        threatSignal.setAdditionalProperty("EMITTER IP", WhydahUtil.getMyIPAddresssesString());
        threatSignal.setInstant(Instant.now().toString());
        threatSignal.setText(text);
        if (clientIpAddress != null && !clientIpAddress.equals("")) {
            threatSignal.setAdditionalProperty("SUSPECT'S IP", clientIpAddress);
        }
        threatSignal.setAdditionalProperty("IMMEDIATE THREAT", true);
        threatSignal.setSignalSeverity(severity.toString());
        threatSignal.setSource(source);
        if (additionalProperties != null) {
            for (int i = 0; i < additionalProperties.length; i = i + 2) {
                String key = additionalProperties[i].toString();
                Object value = (i + 1 == additionalProperties.length) ? "" : additionalProperties[i + 1];
                threatSignal.setAdditionalProperty(key, value);
            }
        }

        return threatSignal;
    }


    public static ThreatSignal createThreat(String clientIpAddress, String source, String text) {
        return createThreat(clientIpAddress, source, text, null, SeverityLevel.LOW, true);
    }

    public static ThreatSignal createThreat(String clientIpAddress, String source, String text, Object[] details) {
        return createThreat(clientIpAddress, source, text, details, SeverityLevel.LOW, true);
    }

    public static ThreatSignal createThreat(String clientIpAddress, String source, String text, Object[] details, SeverityLevel severity) {
        return createThreat(clientIpAddress, source, text, details, severity, true);
    }

    public static ThreatSignal createThreat(String text) {
        return createThreat("", "", text, null, SeverityLevel.LOW, true);
    }

    public static ThreatSignal createThreat(String text, Object[] details) {
        return createThreat("", "", text, details, SeverityLevel.LOW, true);
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
        if (uas == null || uas.length() < 8 || applicationToken == null) {
            log.warn("Called updateAppLinks without was initialized uas: {}, applicationToken: {} - aborting", uas, applicationToken);
            return;
        }
        URI userAdminServiceUri = URI.create(uas);

        if (applications == null || applications.size() < 2) {
            String applicationsJson = new CommandListApplications(userAdminServiceUri, applicationToken.getApplicationTokenId()).execute();
            log.debug("WAS: updateApplinks (initial): AppLications returned:" + applicationsJson);
            if (applicationsJson != null) {
                if (applicationsJson.length() > 20) {
                    setAppLinks(ApplicationMapper.fromJsonList(applicationsJson));
                }
            }

        }
        if ((ApplicationModelUtil.shouldUpdate(5) || getApplicationList() == null || getApplicationList().size() < 2) && applicationToken != null) {
            String applicationsJson = new CommandListApplications(userAdminServiceUri, applicationToken.getApplicationTokenId()).execute();
            log.debug("WAS: updateApplinks: AppLications returned:" + first50(applicationsJson));
            if (applicationsJson != null) {
                if (applicationsJson.length() > 20) {
                    setAppLinks(ApplicationMapper.fromJsonList(applicationsJson));
                }
            }
        }
    }

    public boolean hasApplicationMetaData() {
        if (applications == null) {
            return false;
        }
        return getApplicationList().size() > 2;
    }

    public void updateApplinks(boolean forceUpdate) {
        if (disableUpdateAppLink) {
            return;
        }
        if (uas == null || uas.length() < 8) {
            log.warn("Calling updateAppLinks without was initialized");
            return;
        }
        URI userAdminServiceUri = URI.create(uas);

        if (forceUpdate && applicationToken != null) {
            String applicationsJson = new CommandListApplications(userAdminServiceUri, applicationToken.getApplicationTokenId()).execute();
            log.debug("WAS: updateApplinks: AppLications returned:" + first50(applicationsJson));
            if (applicationsJson != null) {
                if (applicationsJson.length() > 20) {
                    setAppLinks(ApplicationMapper.fromJsonList(applicationsJson));
                }
            }
        }
    }

    private void startThreadAndUpdateAppLinks() {
        if (disableUpdateAppLink) {
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

    /**
     * @return the threatObserver
     */
    public ThreatObserver getThreatObserver() {
        return threatObserver;
    }


}
