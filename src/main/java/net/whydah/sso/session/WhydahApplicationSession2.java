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
import net.whydah.sso.session.baseclasses.CryptoUtil;
import net.whydah.sso.session.baseclasses.ExchangeableKey;
import net.whydah.sso.user.helpers.UserTokenXpathHelper;
import net.whydah.sso.user.types.UserToken;
import net.whydah.sso.util.Lock;
import net.whydah.sso.util.WhydahUtil;
import net.whydah.sso.util.backoff.BackOffExecution;
import net.whydah.sso.util.backoff.JitteryExponentialBackOff;
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static net.whydah.sso.util.LoggerUtil.first50;

public class WhydahApplicationSession2 {

    private static final Logger log = LoggerFactory.getLogger(WhydahApplicationSession2.class);

    public static final int APPLICATION_SESSION_CHECK_INTERVAL_IN_SECONDS = 10;  // Check every 10 seconds to adapt quickly
    public static final int APPLICATION_UPDATE_CHECK_INTERVAL_IN_SECONDS = 60;  // Check every 60 seconds to adapt quickly

    // TODO refactor this to be Whydah generic
    public static final String INN_WHITE_LIST = "INNWHITELIST";

    private static class WhydahApplicationSession2Singleton {
        private final static WhydahApplicationSession2 instance;

        static {
            WAS2Configuration was2Configuration = was2InitializationConfigurationRef.get();
            if (was2Configuration == null) {
                throw new IllegalStateException("was2InitializationConfigurationRef was not set");
            }
            instance = new WhydahApplicationSession2(was2Configuration.sts, was2Configuration.uas, was2Configuration.appCred);
        }

        private static WhydahApplicationSession2 getInstance() {
            return instance;
        }
    }

    private static class WAS2Configuration {
        private final String sts;
        private final String uas;
        private final ApplicationCredential appCred;

        private WAS2Configuration(String sts, String uas, ApplicationCredential appCred) {
            this.sts = sts;
            this.uas = uas;
            this.appCred = appCred;
        }
    }

    private static final AtomicReference<WAS2Configuration> was2InitializationConfigurationRef = new AtomicReference<>();

    //HUY: NO NEED, renewWAS2() will take care of this sleeping nature
    //private static final int[] FIBONACCI = new int[]{0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144};


    public static WhydahApplicationSession2 getInstance(String sts, ApplicationCredential appCred) {
        log.info("WAS2 getInstance(String sts, ApplicationCredential appCred) called");
        was2InitializationConfigurationRef.set(new WAS2Configuration(sts, null, appCred));
        return WhydahApplicationSession2Singleton.getInstance();
    }

    public static WhydahApplicationSession2 getInstance(String sts, String uas, ApplicationCredential appCred) {
        log.info("WAS2 getInstance(String sts, String uas, ApplicationCredential appCred) called");
        was2InitializationConfigurationRef.set(new WAS2Configuration(sts, uas, appCred));
        return WhydahApplicationSession2Singleton.getInstance();
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

    public static ThreatSignal createThreat(String clientIpAddress, String source, String text, Object[] additionalProperties, SeverityLevel severity, boolean isImmediateThreat) {
        ThreatSignal threatSignal = new ThreatSignal();
        if (was2InitializationConfigurationRef.get() != null) {
            WhydahApplicationSession2 instance = WhydahApplicationSession2Singleton.getInstance();
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

    private static class ProtectedApplications {
        private final List<Application> applications = new LinkedList<>();

        public List<Application> getCopy() {
            synchronized (applications) {
                return new ArrayList<>(applications);
            }
        }

        private void set(List<Application> newapplications) {
            synchronized (applications) {
                applications.clear();
                applications.addAll(newapplications);
            }
        }

        public boolean hasMetaData() {
            synchronized (applications) {
                return applications.size() > 2;
            }
        }
    }

    private final ProtectedApplications protectedApplications = new ProtectedApplications();

    private final String sts;
    private final String uas;
    private final ApplicationCredential myAppCredential;

    private final AtomicReference<ApplicationToken> applicationTokenRef = new AtomicReference<>();
    private final AtomicReference<DEFCON> defcon = new AtomicReference<>(DEFCON.DEFCON5);

    private final AtomicBoolean disableUpdateAppLink = new AtomicBoolean(false);

    private final ScheduledExecutorService renew_scheduler;
    private final ScheduledExecutorService app_update_scheduler;

    protected WhydahApplicationSession2(String sts, String uas, ApplicationCredential myAppCredential) {
        log.info("WAS2 initializing: sts:{},  uas:{}, myAppCredential:{}", sts, uas, myAppCredential);

        this.renew_scheduler = Executors.newScheduledThreadPool(1);
        this.app_update_scheduler = Executors.newScheduledThreadPool(1);
        this.sts = sts;
        this.uas = uas;
        this.myAppCredential = myAppCredential;

        // log on
        logOnApp();

        //a loop to renew the app token
        renew_scheduler.schedule(this::doRenewSessionTask, 5, TimeUnit.SECONDS); // initial renew task

        //a loop to update applications
        if (!disableUpdateAppLink.get() && uas != null && uas.length() > 8) { //UAS will skip this check since it has uas=null
            app_update_scheduler.schedule(this::doUpdateApplicationsTask, 5, TimeUnit.SECONDS);
        }
    }

    private void doRenewSessionTask() {
        try {
            renewWAS2();
        } catch (Exception ex) {
            log.error("", ex);
        } finally {
            renew_scheduler.schedule(this::doRenewSessionTask, APPLICATION_SESSION_CHECK_INTERVAL_IN_SECONDS, TimeUnit.SECONDS);
        }
    }

    private void doUpdateApplicationsTask() {
        try {
            updateApplinks();
        } catch (Exception ex) {
            log.error("", ex);
        } finally {
            app_update_scheduler.schedule(this::doUpdateApplicationsTask, APPLICATION_UPDATE_CHECK_INTERVAL_IN_SECONDS, TimeUnit.SECONDS);
        }
    }

    public ApplicationCredential getMyApplicationCredential() {
        return myAppCredential;
    }

    public ApplicationToken getActiveApplicationToken() {
        ApplicationToken applicationToken = applicationTokenRef.get();
        return applicationToken;
    }

    public String getActiveApplicationTokenId() {
        ApplicationToken applicationToken = applicationTokenRef.get();
        if (applicationToken == null) {
            return "";
        }
        return applicationToken.getApplicationTokenId();
    }

    public String getActiveApplicationName() {
        ApplicationToken applicationToken = applicationTokenRef.get();
        if (applicationToken == null) {
            return "N/A";
        }
        return applicationToken.getApplicationName();
    }

    public String getActiveApplicationTokenXML() {
        ApplicationToken applicationToken = applicationTokenRef.get();
        if (applicationToken == null) {
            log.warn("WAS: Unable to initialize new Application Session - no ApplicationToken returned");
            return "";
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
        return defcon.get();
    }

    public void setDefcon(DEFCON defcon) {
        this.defcon.set(defcon);
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
        this.defcon.set(defcon);
        DEFCONHandler.handleDefcon(defcon);
    }

    public void resetApplicationSession() {
        setApplicationToken(null);
        logOnApp();
    }

    public void setApplicationToken(ApplicationToken myApplicationToken) {
        applicationTokenRef.set(myApplicationToken);
    }


    private void renewWAS2() {
        log.trace("Renew WAS: Renew application session called");
        ApplicationToken applicationToken = applicationTokenRef.get();
        if (!hasActiveSession() || !WhydahUtil.isAdminSdk()) {
            log.trace("Renew WAS: checkActiveSession() == false - initializeWAS2 called");
            if (applicationToken == null) {
                log.info("Renew WAS: No active application session, applicationToken:null, myAppCredential:{}, logonAttemptNo:{}", myAppCredential);
            }
            logOnApp();
        } else {
            log.trace("Renew WAS: Active application session found, applicationTokenId: {},  applicationID: {},  expires: {}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());
            Long expires = Long.parseLong(applicationToken.getExpires());
            if (expiresBeforeNextSchedule(expires)) {
                JitteryExponentialBackOff extendSessionBackoff = new JitteryExponentialBackOff(3_000, 1.5, 1000);
                extendSessionBackoff.setMaxInterval(10_000);
                BackOffExecution extendSessionBackoffExecution = extendSessionBackoff.start();
                JitteryExponentialBackOff renewAttemptBackoff = new JitteryExponentialBackOff(1_000, 2, 200);
                renewAttemptBackoff.setMaxInterval(10_000);
                BackOffExecution renewAttemptBackOffExecution = renewAttemptBackoff.start();
                log.info("Renew WAS: Active session expires before next check, re-new - applicationTokenId: {},  applicationID: {},  expires: {}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());
                for (int n = 0; n < 5; n++) {
                    applicationToken = applicationTokenRef.get();
                    String applicationTokenXML = WhydahUtil.extendApplicationSession(sts, getActiveApplicationTokenId(), (int) extendSessionBackoffExecution.nextBackOff());  // Wait a bit longer on retries
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
                            if (logOnApp()) {
                                break;
                            }
                        }
                    }
                    try {
                        Thread.sleep(renewAttemptBackOffExecution.nextBackOff());
                    } catch (InterruptedException ie) {
                    }
                }
            }
        }
    }

    final Lock logon_lock = new Lock();

    private boolean logOnApp() {
        if (!logon_lock.isLocked()) {
            try {
                logon_lock.lock();
            } catch (InterruptedException e) {
                log.error("", e);
                return false;
            }
            try {
                String applicationTokenXML = WhydahUtil.logOnApplication(sts, myAppCredential);
                if (checkApplicationToken(applicationTokenXML)) {
                    setApplicationSessionParameters(applicationTokenXML);
                    ApplicationToken applicationToken = applicationTokenRef.get();
                    log.info("logOnApp : Initialized new application session, applicationTokenId:{}, applicationID: {}, applicationName: {}, expires: {}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getApplicationName(), applicationToken.getExpiresFormatted());
                    return true;
                } else {
                    log.warn("logOnApp : Error, unable to initialize new application session, reset application session  applicationTokenXml: {}", first50(applicationTokenXML));
                    removeApplicationSessionParameters();
                    return false;
                }
            } catch (Exception ex) {
                log.error("", ex);
            } finally {
                logon_lock.unlock();
            }
        }
        return false;
    }

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
                log.info("WAS - setApplicationSessionParameters : New application session created for applicationID: {}, applicationTokenID: {}, expires: {}, key:{}", applicationToken.getApplicationID(), applicationToken.getApplicationTokenId(), applicationToken.getExpiresFormatted(), CryptoUtil.getActiveKey());

            } catch (Exception e) {
                log.warn("Unable to update CryptoUtil with new cryptokey", e);
            }
        } else {
            log.error("WAS - No key found for applicationID: {}, applicationTokenID: {}, expires: {}", applicationToken.getApplicationID(), applicationToken.getApplicationTokenId(), applicationToken.getExpiresFormatted());
        }
    }

    private void removeApplicationSessionParameters() {
        setApplicationToken(null);
    }

    /**
     * @return true is session is active and working
     */
    public boolean checkActiveSession() {
        return hasActiveSession();
    }

    private final AtomicLong lastTimeChecked = new AtomicLong(0);

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
            boolean hasActiveSession = commandValidateApplicationTokenId.execute();
            lastTimeChecked.set(System.currentTimeMillis()); //last time check is et after the command is executed

            if (!hasActiveSession) {

                if (commandValidateApplicationTokenId.isResponseFromFallback()) {
                    log.warn("Got timeout on call to verify applicationTokenID, since applicationtoken is not expired, we return true");
                    return true;
                }
                log.info("WAS: applicationsession invalid from STS, reset application session, applicationTokenId: {} - for applicationID: {} - expires:{}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());
                removeApplicationSessionParameters();
            }
        }

        return true;

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


    /**
     * Application cache section - keep a cache of configured applications
     */

    public List<Application> getApplicationList() {
        return protectedApplications.getCopy();
    }

    private void setAppLinks(List<Application> newapplications) {
        protectedApplications.set(newapplications);
    }

    final Lock updateLock = new Lock();

    public void updateApplinks() {
        if (disableUpdateAppLink.get()) {
            return;
        }
        if (!updateLock.isLocked()) {
            try {
                updateLock.lock();
                ApplicationToken applicationToken = applicationTokenRef.get();
                if (uas == null || uas.length() < 8 || applicationToken == null) {
                    log.warn("Called updateAppLinks without was initialized uas: {}, applicationToken: {} - aborting", uas, applicationToken);
                    return;
                }
                URI userAdminServiceUri = URI.create(uas);

                String applicationsJson = new CommandListApplications(userAdminServiceUri, applicationToken.getApplicationTokenId()).execute();
                log.debug("WAS: updateApplinks: AppLications returned:" + first50(applicationsJson));
                if (applicationsJson != null) {
                    if (applicationsJson.length() > 20) {
                        setAppLinks(ApplicationMapper.fromJsonList(applicationsJson));
                    }
                }
            } catch (Exception ex) {
                log.error("", ex);
            } finally {
                updateLock.unlock();
            }
        }
    }

    public boolean hasApplicationMetaData() {
        return protectedApplications.hasMetaData();
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
        List<Application> applications = protectedApplications.getCopy();
        for (Application app : applications) {
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
