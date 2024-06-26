package net.whydah.sso.session;

import net.whydah.sso.application.mappers.ApplicationMapper;
import net.whydah.sso.application.mappers.ApplicationTagMapper;
import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.Application;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.application.types.Tag;
import net.whydah.sso.commands.appauth.CommandGetApplicationKey;
import net.whydah.sso.commands.appauth.CommandValidateApplicationTokenId;
import net.whydah.sso.commands.application.CommandListApplications;
import net.whydah.sso.commands.threat.CommandSendThreatSignal;
import net.whydah.sso.ddd.model.application.ApplicationTokenExpires;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static net.whydah.sso.util.LoggerUtil.first50;

public class WhydahApplicationSessionOld {

    public static final String INN_WHITE_LIST = "INNWHITELIST";
    public static final int APPLICATION_SESSION_CHECK_INTERVAL_IN_SECONDS = 10;  // Check every 30 seconds to adapt quickly
    public static final int APPLICATION_UPDATE_CHECK_INTERVAL_IN_SECONDS = 10;  // Check every 30 seconds to adapt quickly

    private static final Logger log = LoggerFactory.getLogger(WhydahApplicationSessionOld.class);

    private static final AtomicReference<WhydahApplicationSessionOld> instanceRef = new AtomicReference<>();

    private final List<Application> applications = new LinkedList<>();
    private final String sts;
    private final String uas;

    private final ApplicationCredential myAppCredential;

    private final AtomicInteger logonAttemptNo = new AtomicInteger(0);

    private final AtomicReference<ApplicationToken> applicationTokenRef = new AtomicReference<>();
    private final AtomicReference<DEFCON> defconRef = new AtomicReference<>(DEFCON.DEFCON5);
    private final AtomicBoolean disableUpdateAppLink = new AtomicBoolean(false);

    //HUY: NO NEED, renewWhydahApplicationSession() will take care of this sleeping nature
    //private static final int[] FIBONACCI = new int[]{0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144};
    //private ScheduledExecutorService initialize_scheduler;
    private final ScheduledExecutorService renew_scheduler;
    private final ScheduledExecutorService app_update_scheduler;

    private final AtomicBoolean isInitialized = new AtomicBoolean(false);
    // TODO  refactor this to be Whydah generic

    /**
     * Protected singleton constructors
     */
    protected WhydahApplicationSessionOld(String sts, String uas, ApplicationCredential myAppCredential) {
        log.info("WhydahApplicationSession initializing: sts:{},  uas:{}, myAppCredential:{}", sts, uas, myAppCredential);
        //this.initialize_scheduler = Executors.newScheduledThreadPool(1);
        this.renew_scheduler = Executors.newScheduledThreadPool(1);
        this.app_update_scheduler = Executors.newScheduledThreadPool(1);
        this.sts = sts;
        this.uas = uas;
        this.myAppCredential = myAppCredential;

        //register more if any
        //try log-on first
        initializeWhydahApplicationSession();
        renew_scheduler.scheduleAtFixedRate(
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

        //update application list
        //used for STS, OIDSSO and other services
        if (!disableUpdateAppLink.get() && uas != null && uas.length() > 8 && applicationTokenRef.get() != null) { //UAS will skip this check since it has uas=null
            app_update_scheduler.scheduleAtFixedRate(
                    new Runnable() {
                        public void run() {
                            try {
                                updateApplinks();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    },
                    5, APPLICATION_UPDATE_CHECK_INTERVAL_IN_SECONDS, TimeUnit.SECONDS);
        }
    }


    public static WhydahApplicationSessionOld getInstance(String sts, ApplicationCredential appCred) {
        log.info("WhydahApplicationSession getInstance(String sts, ApplicationCredential appCred) called");
        WhydahApplicationSessionOld instance = instanceRef.get();
        if (instance == null) {
            synchronized (instanceRef) {
                instance = instanceRef.get();
                if (instance == null) {
                    instance = new WhydahApplicationSessionOld(sts, null, appCred);
                    instanceRef.set(instance);
                }
            }
        }
        return instance;
    }

    public static WhydahApplicationSessionOld getInstance(String sts, String uas, ApplicationCredential appCred) {
        log.info("WhydahApplicationSession getInstance(String sts, String uas, ApplicationCredential appCred) called");
        WhydahApplicationSessionOld instance = instanceRef.get();
        if (instance == null) {
            synchronized (instanceRef) {
                instance = instanceRef.get();
                if (instance == null) {
                    instance = new WhydahApplicationSessionOld(sts, uas, appCred);
                    instanceRef.set(instance);
                }
            }
        }
        return instance;
    }


    public ApplicationCredential getMyApplicationCredential() {
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
        ApplicationToken applicationToken = applicationTokenRef.get();
        if (applicationToken == null) {
            //            initializeWhydahApplicationSession();
        }
        return applicationToken;
    }

    public String getActiveApplicationTokenId() {
        ApplicationToken applicationToken = applicationTokenRef.get();
        if (applicationToken == null) {
            //            initializeWhydahApplicationSession();
        }
        if (applicationToken == null) {
            return "";
        }
        return applicationToken.getApplicationTokenId();
    }

    public String getActiveApplicationName() {
        ApplicationToken applicationToken = applicationTokenRef.get();
        if (applicationToken == null) {
            //            initializeWhydahApplicationSession();
        }
        if (applicationToken == null) {
            return "N/A";
        }
        return applicationToken.getApplicationName();
    }

    public String getActiveApplicationTokenXML() {
        ApplicationToken applicationToken = applicationTokenRef.get();
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
        return defconRef.get();
    }

    public void setDefcon(DEFCON defcon) {
        this.defconRef.set(defcon);
        DEFCONHandler.handleDefcon(defcon);

    }

    public boolean hasUASAccessAdminRole(UserToken userToken) {
        return WhydahUtil.hasUASAccessAdminRole(userToken);
    }

    public void updateDefcon(String userTokenXml) {
        String tokendefcon = UserTokenXpathHelper.getDEFCONLevel(userTokenXml);
        DEFCON defcon;
        try {
            defcon = DEFCON.valueOf(tokendefcon);
        } catch (IllegalArgumentException e) {
            return;
        }
        if (defcon != DEFCON.DEFCON5) {
            log.warn("DEFCON level is now {}", defcon);
        }
        this.defconRef.set(defcon);
        DEFCONHandler.handleDefcon(defcon);
    }

    public void resetApplicationSession() {
        setApplicationToken(null);
        isInitialized.set(false);
        initializeWhydahApplicationSession();
    }

    public void setApplicationToken(ApplicationToken myApplicationToken) {
        applicationTokenRef.set(myApplicationToken);
    }


    private void renewWhydahApplicationSession() {
        log.trace("Renew WAS: Renew application session called");
        if (!hasActiveSession()) {
            log.trace("Renew WAS: checkActiveSession() == false - initializeWhydahApplicationSession called");
            ApplicationToken applicationToken = applicationTokenRef.get();
            if (applicationToken == null) {
                log.info("Renew WAS: No active application session, applicationToken:null, myAppCredential:{}, logonAttemptNo:{}", myAppCredential, logonAttemptNo.get());
            }
            initializeWhydahApplicationSession();
        } else {
            ApplicationToken applicationToken = applicationTokenRef.get();
            log.trace("Renew WAS: Active application session found, applicationTokenId: {},  applicationID: {},  expires: {}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());

            Long expires = Long.parseLong(applicationToken.getExpires());
            if (expiresBeforeNextSchedule(expires)) {
                log.info("Renew WAS: Active session expires before next check, re-new - applicationTokenId: {},  applicationID: {},  expires: {}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());
                for (int n = 0; n < 5; n++) {
                    String applicationTokenXML = WhydahUtil.extendApplicationSession(sts, getActiveApplicationTokenId(), 2000 + n * 1000);  // Wait a bit longer on retries
                    if (applicationTokenXML != null && applicationTokenXML.length() > 10) {
                        setApplicationToken(ApplicationTokenMapper.fromXml(applicationTokenXML));
                        log.info("Renew WAS: Success in renew applicationsession, applicationTokenId: {} - for applicationID: {}, expires: {}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());
                        log.debug("Renew WAS: - expiresAt: {} - now: {} - expires in: {} seconds", applicationToken.getExpires(), System.currentTimeMillis(), (Long.parseLong(applicationToken.getExpires()) - System.currentTimeMillis()) / 1000);
                        String exchangeableKeyString = new CommandGetApplicationKey(URI.create(sts), getActiveApplicationTokenId()).execute();
                        if (exchangeableKeyString != null) {
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
                            //try again now
                            log.error("Key not found exchangeableKeyString{}", exchangeableKeyString);
                        }
                    } else {
                        log.info("Renew WAS: Failed to renew applicationsession, attempt:{}, returned response from STS: {}", n, applicationTokenXML);
                        if (n > 2) {
                            // OK, we wont get a renewed session, so we start a new one
                            initializeWhydahApplicationSession();
                            if (isInitialized.get()) {
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

    private void initializeWhydahApplicationSession() {
        if (isInitialized.get()) {
            return;
        }
        if (!logon_lock.tryLock()) {
            return;
        }
        try {
            logonAttemptNo.incrementAndGet();
            String applicationTokenXML = WhydahUtil.logOnApplication(sts, myAppCredential);
            if (checkApplicationToken(applicationTokenXML)) {
                isInitialized.set(true);
                setApplicationSessionParameters(applicationTokenXML);
                ApplicationToken applicationToken = applicationTokenRef.get();
                log.info("InitWAS {}: Initialized new application session, applicationTokenId:{}, applicationID: {}, applicationName: {}, expires: {}", logonAttemptNo.get(), applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getApplicationName(), applicationToken.getExpiresFormatted());
                logonAttemptNo.set(0);
            } else {

                //NOTHING TO DO, renewWhydahApplicationSession() will activate log-on again

                log.warn("InitWAS {}: Error, unable to initialize new application session,, reset application session  applicationTokenXml: {}", logonAttemptNo.get(), first50(applicationTokenXML));
                removeApplicationSessionParameters();


                //			ScheduledFuture<?> sf = initialize_scheduler.schedule(
                //					new Runnable() {
                //						public void run() {
                //							try {
                //								initializeWhydahApplicationSessionThread();
                //							} catch (Exception ex) {
                //								ex.printStackTrace();
                //							}
                //						}
                //					}, 5, TimeUnit.SECONDS);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            logon_lock.unlock();
        }
    }

    final Lock logon_lock = new ReentrantLock();
    //	private boolean initializeWhydahApplicationSessionThread() {
    //		log.info("Initializing new application session {} with applicationCredential: {}", logonAttemptNo, myAppCredential);
    //
    //		try {
    //			Thread.sleep(FIBONACCI[logonAttemptNo] * 10000);
    //		} catch (InterruptedException e) {
    //			throw new RuntimeException(e);
    //		}
    //		if (!logon_lock.isLocked()) {
    //			if (isInitialized) {
    //				return true;
    //			}
    //			if (logonAttemptNo == 0) {
    //				return true;
    //			}
    //			logonAttemptNo++;
    //			String applicationTokenXML = WhydahUtil.logOnApplication(sts, myAppCredential);
    //			if (!checkApplicationToken(applicationTokenXML)) {
    //				if (logonAttemptNo > 12) {
    //					logonAttemptNo = 1;
    //				}
    //				log.warn("InitWAS {}: Error, unable to initialize new application session,, reset application session  applicationTokenXml: {}", logonAttemptNo, first50(applicationTokenXML));
    //				removeApplicationSessionParameters();
    //				return false;
    //			}
    //			isInitialized = true;
    //			setApplicationSessionParameters(applicationTokenXML);
    //			log.info("InitWAS {}: Initialized new application session, applicationTokenId:{}, applicationID: {}, applicationName: {}, expires: {}", logonAttemptNo, applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getApplicationName(), applicationToken.getExpiresFormatted());
    //			logonAttemptNo = 0;
    //		}
    //		return true;
    //	}

    private void setApplicationSessionParameters(String applicationTokenXML) {
        setApplicationToken(ApplicationTokenMapper.fromXml(applicationTokenXML));
        ApplicationToken applicationToken = applicationTokenRef.get();
        String exchangeableKeyString = new CommandGetApplicationKey(URI.create(sts), applicationToken.getApplicationTokenId()).execute();

        if (exchangeableKeyString != null && exchangeableKeyString.length() > 10) {
            try {
                log.debug("Found exchangeableKeyString: {}", exchangeableKeyString);
                ExchangeableKey exchangeableKey = new ExchangeableKey(exchangeableKeyString);
                log.debug("Found exchangeableKey: {}", exchangeableKey);
                CryptoUtil.setExchangeableKey(exchangeableKey);
                log.info("WAS - setApplicationSessionParameters {}: New application session created for applicationID: {}, applicationTokenID: {}, expires: {}, key:{}", logonAttemptNo.get(), applicationToken.getApplicationID(), applicationToken.getApplicationTokenId(), applicationToken.getExpiresFormatted(), CryptoUtil.getActiveKey());

            } catch (Exception e) {
                log.warn("Unable to update CryptoUtil with new cryptokey", e);
            }
        } else {
            log.error("WAS {}- No key found for applicationID: {}, applicationTokenID: {}, expires: {}", logonAttemptNo.get(), applicationToken.getApplicationID(), applicationToken.getApplicationTokenId(), applicationToken.getExpiresFormatted());
        }
        isInitialized.set(true);
    }

    private void removeApplicationSessionParameters() {
        setApplicationToken(null);
        isInitialized.set(false);
        log.info("WAS {}: Application session removed for applicationID: {} applicationName: {},", logonAttemptNo.get(), myAppCredential.getApplicationID(), myAppCredential.getApplicationName());
    }

    /**
     * @return true is session is active and working
     */
    public boolean checkActiveSession() {
        return hasActiveSession();
    }

    private final AtomicLong lastTimeChecked = new AtomicLong(0);
    private final AtomicBoolean hasActiveSession = new AtomicBoolean(false);

    /**
     * @return true is session is active and working
     */
    public boolean hasActiveSession() {
        ApplicationToken applicationToken = applicationTokenRef.get();
        if (applicationToken == null || !ApplicationTokenID.isValid(getActiveApplicationTokenId())) {
            removeApplicationSessionParameters();
            return false;
        }
        if (!ApplicationTokenExpires.isValid(applicationToken.getExpires())) {
            log.info("WAS: applicationsession timeout, reset application session, applicationTokenId: {} - for applicationID: {} - expires:{}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());
            removeApplicationSessionParameters();
            return false;
        }

        //don't call this check too often
        if (System.currentTimeMillis() - lastTimeChecked.get() > APPLICATION_SESSION_CHECK_INTERVAL_IN_SECONDS * 1000) {

            CommandValidateApplicationTokenId commandValidateApplicationTokenId = new CommandValidateApplicationTokenId(getSTS(), getActiveApplicationTokenId());
            hasActiveSession.set(commandValidateApplicationTokenId.execute());
            lastTimeChecked.set(System.currentTimeMillis()); //last time check is et after the command is executed

            if (!hasActiveSession.get()) {

                if (commandValidateApplicationTokenId.isResponseFromFallback()) {
                    log.warn("Got timeout on call to verify applicationTokenID, since applicationtoken is not expired, we return true");
                    return true;
                }
                log.info("WAS: applicationsession invalid from STS, reset application session, applicationTokenId: {} - for applicationID: {} - expires:{}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());
                removeApplicationSessionParameters();
            }
        }

        return hasActiveSession.get();

    }

    /**
     * @return true if applicationTokenXML seems sensible
     */
    public boolean checkApplicationToken(String applicationTokenXML) {
        try {
            ApplicationToken at = ApplicationTokenMapper.fromXml(applicationTokenXML);
            if (ApplicationTokenID.isValid(at.getApplicationTokenId())) {
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
        WhydahApplicationSessionOld instance = instanceRef.get();
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
        synchronized (applications) {
            return new ArrayList<>(applications);
        }
    }

    private void setAppLinks(List<Application> newapplications) {
        synchronized (applications) {
            applications.clear();
            applications.addAll(newapplications);
        }
    }

    final Lock updateLock = new ReentrantLock();

    public void updateApplinks() {
        if (disableUpdateAppLink.get() || !WhydahUtil.isAdminSdk()) {
            return;
        }
        if (!updateLock.tryLock()) {
            return;
        }
        try {
            ApplicationToken applicationToken = applicationTokenRef.get();
            if (uas == null || uas.length() < 8 || applicationToken == null) {
                log.warn("Called updateAppLinks without was initialized uas: {}, applicationToken: {} - aborting", uas, applicationToken);
                return;
            }
            URI userAdminServiceUri = URI.create(uas);

            if ((ApplicationModelUtil.shouldUpdate(5) || getApplicationList() == null || getApplicationList().size() < 2) && applicationToken != null) {
                String applicationsJson = new CommandListApplications(userAdminServiceUri, applicationToken.getApplicationTokenId()).execute();
                log.debug("WAS: updateApplinks: AppLications returned:" + first50(applicationsJson));
                if (applicationsJson != null) {
                    if (applicationsJson.length() > 20) {
                        setAppLinks(ApplicationMapper.fromJsonList(applicationsJson));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            updateLock.unlock();
        }
    }

    public boolean hasApplicationMetaData() {
        return getApplicationList().size() > 2;
    }

    public void updateApplinks(boolean forceUpdate) {
        if (disableUpdateAppLink.get()) {
            return;
        }
        if (uas == null || uas.length() < 8) {
            log.warn("Calling updateAppLinks without was initialized");
            return;
        }
        URI userAdminServiceUri = URI.create(uas);

        ApplicationToken applicationToken = applicationTokenRef.get();
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

    public boolean isDisableUpdateAppLink() {
        return disableUpdateAppLink.get();
    }

    public void setDisableUpdateAppLink(boolean disableUpdateAppLink) {
        this.disableUpdateAppLink.set(disableUpdateAppLink);
    }


    public boolean isWhiteListed(String suspect) {
        for (Application app : getApplicationList()) {
            if (app.getId().equals(getMyApplicationCredential().getApplicationID())) {

                if (app.getTags() != null && app.getTags().length() > 0 && app.getTags().contains(INN_WHITE_LIST)) {
                    List<Tag> tagList = ApplicationTagMapper.getTagList(app.getTags());
                    for (Tag tag : tagList) {
                        if (tag.getName().equalsIgnoreCase(INN_WHITE_LIST) && tag.getValue() != null && tag.getValue().length() > 0) {

                            String[] ids = tag.getValue().split("\\s*[,;:\\s+]\\s*");
                            for (String id : ids) {
                                if (id.equalsIgnoreCase(suspect)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

}
