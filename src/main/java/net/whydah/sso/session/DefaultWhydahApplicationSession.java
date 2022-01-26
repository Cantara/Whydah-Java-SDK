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
import net.whydah.sso.session.experimental.WhydahApplicationSession3;
import net.whydah.sso.user.helpers.UserTokenXpathHelper;
import net.whydah.sso.user.types.UserToken;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static net.whydah.sso.util.LoggerUtil.first50;

public class DefaultWhydahApplicationSession implements WhydahApplicationSession, WhydahApplicationSession2, WhydahApplicationSession3 {

    private static final Logger log = LoggerFactory.getLogger(DefaultWhydahApplicationSession.class);

    // TODO refactor this to be Whydah generic
    private static final String INN_WHITE_LIST = "INNWHITELIST";

    //HUY: NO NEED, renewWAS() will take care of this sleeping nature
    //private static final int[] FIBONACCI = new int[]{0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144};

    /*
     * This class exists to be able to test this functionality as a separate unit
     */
    static class ExpireChecker {
        private final int applicationSessionCheckIntervalInSeconds;

        ExpireChecker(int applicationSessionCheckIntervalInSeconds) {
            this.applicationSessionCheckIntervalInSeconds = applicationSessionCheckIntervalInSeconds;
        }

        public boolean expiresBeforeNextScheduledSessionCheck(Long timestamp) {
            long currentTime = System.currentTimeMillis();
            long expiresAt = (timestamp);
            long diffSeconds = (expiresAt - currentTime) / 1000;
            log.debug("expiresBeforeNextSchedule - expiresAt: {} - now: {} - expires in: {} seconds", expiresAt, currentTime, diffSeconds);
            if (diffSeconds < (applicationSessionCheckIntervalInSeconds * 3L)) {
                log.debug("expiresBeforeNextSchedule - re-new application session.. diffseconds: {}", diffSeconds);
                return true;
            }
            return false;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        static final int DEFAULT_APPLICATION_SESSION_CHECK_INTERVAL_IN_SECONDS = 10;  // Check every 10 seconds to adapt quickly
        static final int DEFAULT_APPLICATION_UPDATE_CHECK_INTERVAL_IN_SECONDS = 60;  // Check every 60 seconds to adapt quickly

        private String sts;
        private String uas;
        private ApplicationCredential appCred;
        private int applicationSessionCheckIntervalInSeconds = DEFAULT_APPLICATION_SESSION_CHECK_INTERVAL_IN_SECONDS;
        private int applicationUpdateCheckIntervalInSeconds = DEFAULT_APPLICATION_UPDATE_CHECK_INTERVAL_IN_SECONDS;

        private Builder() {
        }

        public Builder withSts(String sts) {
            this.sts = sts;
            return this;
        }

        public Builder withUas(String uas) {
            this.uas = uas;
            return this;
        }

        public Builder withAppCred(ApplicationCredential appCred) {
            this.appCred = appCred;
            return this;
        }

        public Builder withApplicationSessionCheckIntervalInSeconds(int applicationSessionCheckIntervalInSeconds) {
            this.applicationSessionCheckIntervalInSeconds = applicationSessionCheckIntervalInSeconds;
            return this;
        }

        public Builder withApplicationUpdateCheckIntervalInSeconds(int applicationUpdateCheckIntervalInSeconds) {
            this.applicationUpdateCheckIntervalInSeconds = applicationUpdateCheckIntervalInSeconds;
            return this;
        }

        public DefaultWhydahApplicationSession build() {
            return new DefaultWhydahApplicationSession(sts, uas, appCred, applicationSessionCheckIntervalInSeconds, applicationUpdateCheckIntervalInSeconds);
        }
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

    private final int applicationSessionCheckIntervalInSeconds;
    private final int applicationUpdateCheckIntervalInSeconds;

    private final ScheduledExecutorService renew_scheduler;
    private final ScheduledExecutorService app_update_scheduler;

    private final Lock logon_lock = new ReentrantLock();

    private final AtomicLong lastTimeChecked = new AtomicLong(0);

    private final Lock updateLock = new ReentrantLock();

    private final AtomicBoolean closed = new AtomicBoolean(false);

    protected DefaultWhydahApplicationSession(String sts, String uas, ApplicationCredential myAppCredential, int applicationSessionCheckIntervalInSeconds, int applicationUpdateCheckIntervalInSeconds) {
        log.info("WAS initializing: sts:{},  uas:{}, myAppCredential:{}", sts, uas, myAppCredential);

        this.renew_scheduler = Executors.newScheduledThreadPool(1);
        this.app_update_scheduler = Executors.newScheduledThreadPool(1);
        this.sts = sts;
        this.uas = uas;
        this.myAppCredential = myAppCredential;
        this.applicationSessionCheckIntervalInSeconds = applicationSessionCheckIntervalInSeconds;
        this.applicationUpdateCheckIntervalInSeconds = applicationUpdateCheckIntervalInSeconds;

        // log on
        logOnApp();

        //a loop to renew the app token
        renew_scheduler.schedule(this::doRenewSessionTask, applicationSessionCheckIntervalInSeconds, TimeUnit.SECONDS); // initial renew task

        //a loop to update applications
        if (!disableUpdateAppLink.get() && uas != null && uas.length() > 8) { //UAS will skip this check since it has uas=null
            app_update_scheduler.schedule(this::doUpdateApplicationsTask, Math.max(1, applicationUpdateCheckIntervalInSeconds / 5), TimeUnit.SECONDS);
        }
    }

    private void doRenewSessionTask() {
        if (closed.get()) {
            return; // break loop
        }
        try {
            renewWAS();
        } catch (Exception ex) {
            log.error("", ex);
        } finally {
            renew_scheduler.schedule(this::doRenewSessionTask, applicationSessionCheckIntervalInSeconds, TimeUnit.SECONDS);
        }
    }

    private void doUpdateApplicationsTask() {
        if (closed.get()) {
            return; // break loop
        }
        try {
            updateApplinks();
        } catch (Exception ex) {
            log.error("", ex);
        } finally {
            app_update_scheduler.schedule(this::doUpdateApplicationsTask, applicationUpdateCheckIntervalInSeconds, TimeUnit.SECONDS);
        }
    }

    @Override
    public boolean expiresBeforeNextScheduledSessionCheck(Long timestamp) {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
        return new ExpireChecker(applicationSessionCheckIntervalInSeconds).expiresBeforeNextScheduledSessionCheck(timestamp);
    }

    @Override
    public ThreatSignalBuilder threatSignalBuilder() {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
        return new DefaultThreatSignalBuilder();
    }

    private class DefaultThreatSignalBuilder implements ThreatSignalBuilder {

        private String clientIpAddress = "";
        private String source = "";
        private String text;
        private Object[] additionalProperties;
        private SeverityLevel severity = SeverityLevel.LOW;
        private boolean isImmediateThreat = true;

        private DefaultThreatSignalBuilder() {
        }

        @Override
        public DefaultThreatSignalBuilder withClientIpAddress(String clientIpAddress) {
            this.clientIpAddress = clientIpAddress;
            return this;
        }

        @Override
        public DefaultThreatSignalBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        @Override
        public DefaultThreatSignalBuilder withText(String text) {
            this.text = text;
            return this;
        }

        @Override
        public DefaultThreatSignalBuilder withAdditionalProperties(Object[] additionalProperties) {
            this.additionalProperties = additionalProperties;
            return this;
        }

        @Override
        public DefaultThreatSignalBuilder withSeverity(SeverityLevel severity) {
            this.severity = severity;
            return this;
        }

        @Override
        public DefaultThreatSignalBuilder withImmediateThreat(boolean immediateThreat) {
            isImmediateThreat = immediateThreat;
            return this;
        }

        @Override
        public ThreatSignal build() {
            if (closed.get()) {
                throw new WhydahApplicationSessionClosedException();
            }
            ThreatSignal threatSignal = new ThreatSignal();
            threatSignal.setSignalEmitter(getActiveApplicationName() + " [" + WhydahUtil.getMyIPAddresssesString() + "]");
            threatSignal.setAdditionalProperty("DEFCON", getDefcon());
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
    }

    @Override
    public ApplicationCredential getMyApplicationCredential() {
        return myAppCredential;
    }

    @Override
    public ApplicationToken getActiveApplicationToken() {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
        ApplicationToken applicationToken = applicationTokenRef.get();
        return applicationToken;
    }

    @Override
    public String getActiveApplicationTokenId() {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
        ApplicationToken applicationToken = applicationTokenRef.get();
        if (applicationToken == null) {
            return "";
        }
        return applicationToken.getApplicationTokenId();
    }

    @Override
    public String getActiveApplicationName() {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
        ApplicationToken applicationToken = applicationTokenRef.get();
        if (applicationToken == null) {
            return "N/A";
        }
        return applicationToken.getApplicationName();
    }

    @Override
    public String getActiveApplicationTokenXML() {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
        ApplicationToken applicationToken = applicationTokenRef.get();
        if (applicationToken == null) {
            log.warn("WAS: Unable to initialize new Application Session - no ApplicationToken returned");
            return "";
        }
        return ApplicationTokenMapper.toXML(applicationToken);
    }

    @Override
    public String getSTS() {
        return sts;
    }

    @Override
    public String getUAS() {
        return uas;
    }

    @Override
    public DEFCON getDefcon() {
        return defcon.get();
    }

    @Override
    public void setDefcon(DEFCON defcon) {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
        this.defcon.set(defcon);
        DEFCONHandler.handleDefcon(defcon);
    }

    @Override
    public boolean hasUASAccessAdminRole(UserToken userToken) {
        return WhydahUtil.hasUASAccessAdminRole(userToken);
    }

    @Override
    public void updateDefcon(String userTokenXml) {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
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

    @Override
    public void resetApplicationSession() {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
        setApplicationToken(null);
        logOnApp();
    }

    @Override
    public void setApplicationToken(ApplicationToken myApplicationToken) {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
        applicationTokenRef.set(myApplicationToken);
    }


    private void renewWAS() {
        log.trace("Renew WAS: Renew application session called");
        ApplicationToken applicationToken = applicationTokenRef.get();
        if (!hasActiveSession() || !WhydahUtil.isAdminSdk()) {
            log.trace("Renew WAS: checkActiveSession() == false - initializeWAS called");
            if (applicationToken == null) {
                log.info("Renew WAS: No active application session, applicationToken:null, myAppCredential:{}, logonAttemptNo:{}", myAppCredential);
            }
            logOnApp();
        } else {
            log.trace("Renew WAS: Active application session found, applicationTokenId: {},  applicationID: {},  expires: {}", applicationToken.getApplicationTokenId(), applicationToken.getApplicationID(), applicationToken.getExpiresFormatted());
            Long expires = Long.parseLong(applicationToken.getExpires());
            if (expiresBeforeNextScheduledSessionCheck(expires)) {
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

    private boolean logOnApp() {
        if (!logon_lock.tryLock()) {
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
            }
        } catch (Exception ex) {
            log.error("", ex);
        } finally {
            logon_lock.unlock();
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

    @Override
    public boolean checkActiveSession() {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
        return hasActiveSession();
    }

    @Override
    public boolean hasActiveSession() {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
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
        if (System.currentTimeMillis() - lastTimeChecked.get() > applicationSessionCheckIntervalInSeconds * 1000L) {

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
                return false;
            }
        }

        return true;

    }

    @Override
    public boolean checkApplicationToken(String applicationTokenXML) {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
        try {
            ApplicationToken at = ApplicationTokenMapper.fromXml(applicationTokenXML);
            if (ApplicationTokenID.isValid(at.getApplicationTokenId())) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    @Override
    public void reportThreatSignal(ThreatSignal threatSignal) {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
        try {
            new CommandSendThreatSignal(URI.create(getSTS()), getActiveApplicationTokenId(), threatSignal).queue();
        } catch (Exception e) {

        }
    }

    /*
     * Application cache section - keep a cache of configured applications
     */

    @Override
    public List<Application> getApplicationList() {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
        return protectedApplications.getCopy();
    }

    private void setAppLinks(List<Application> newapplications) {
        protectedApplications.set(newapplications);
    }

    @Override
    public void updateApplinks() {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
        if (disableUpdateAppLink.get()) {
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

    @Override
    public boolean hasApplicationMetaData() {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
        return protectedApplications.hasMetaData();
    }

    @Override
    public void updateApplinks(boolean forceUpdate) {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
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

    @Override
    public boolean isDisableUpdateAppLink() {
        return disableUpdateAppLink.get();
    }

    @Override
    public void setDisableUpdateAppLink(boolean disableUpdateAppLink) {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
        this.disableUpdateAppLink.set(disableUpdateAppLink);
    }

    @Override
    public boolean isWhiteListed(String suspect) {
        if (closed.get()) {
            throw new WhydahApplicationSessionClosedException();
        }
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

    private void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(5, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            shutdownAndAwaitTermination(renew_scheduler);
            shutdownAndAwaitTermination(app_update_scheduler);
        }
    }
}


