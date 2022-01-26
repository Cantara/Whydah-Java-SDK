package net.whydah.sso.session;

import net.whydah.sso.application.types.ApplicationCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

class DefaultWhydahApplicationSessionSingleton {

    private static final Logger log = LoggerFactory.getLogger(DefaultWhydahApplicationSessionSingleton.class);

    private static class Holder {
        private final static DefaultWhydahApplicationSession instance;

        static {
            WASConfiguration wasConfiguration = wasInitializationConfigurationRef.get();
            if (wasConfiguration == null) {
                throw new IllegalStateException("wasInitializationConfigurationRef was not set");
            }
            instance = DefaultWhydahApplicationSession.builder()
                    .withSts(wasConfiguration.sts)
                    .withUas(wasConfiguration.uas)
                    .withAppCred(wasConfiguration.appCred)
                    .build();
        }

        private static DefaultWhydahApplicationSession getInstance() {
            return instance;
        }
    }

    private static class WASConfiguration {
        private final String sts;
        private final String uas;
        private final ApplicationCredential appCred;

        private WASConfiguration(String sts, String uas, ApplicationCredential appCred) {
            this.sts = sts;
            this.uas = uas;
            this.appCred = appCred;
        }
    }

    private static final AtomicReference<WASConfiguration> wasInitializationConfigurationRef = new AtomicReference<>();

    static DefaultWhydahApplicationSession getInstance() {
        log.info("WAS getInstance() called");
        WASConfiguration wasConfiguration = wasInitializationConfigurationRef.get();
        if (wasConfiguration == null) {
            throw new IllegalStateException("WAS singleton was not initialized yet. Ensure that one of the getInstance(...) initialization methods of WhydahApplicationSession/WhydahApplicationSession2 is called before using expiresBeforeNextSchedule(...) or createThreat(...) of WhydahApplicationSession/WhydahApplicationSession2");
        }
        return Holder.getInstance();
    }

    static DefaultWhydahApplicationSession getInstance(String sts, ApplicationCredential appCred) {
        log.info("WAS getInstance(String sts, ApplicationCredential appCred) called");
        wasInitializationConfigurationRef.set(new WASConfiguration(sts, null, appCred));
        return Holder.getInstance();
    }

    static DefaultWhydahApplicationSession getInstance(String sts, String uas, ApplicationCredential appCred) {
        log.info("WAS getInstance(String sts, String uas, ApplicationCredential appCred) called");
        wasInitializationConfigurationRef.set(new WASConfiguration(sts, uas, appCred));
        return Holder.getInstance();
    }
}


