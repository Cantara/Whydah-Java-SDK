package net.whydah.sso.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Get application mode from os environment or system property.
 */
public class ApplicationMode {
    public final static String IAM_MODE_KEY = "IAM_MODE";
    public final static String PROD = "PROD";
    public final static String TEST = "TEST";
    public final static String TEST_LOCALHOST = "TEST_LOCALHOST";
    public final static String DEV = "DEV";

    private static final Logger log = LoggerFactory.getLogger(ApplicationMode.class);


    public static String getApplicationMode() {
        String appMode = System.getenv(IAM_MODE_KEY);
        if(appMode == null) {
            appMode = System.getProperty(IAM_MODE_KEY);
        }
        if(appMode == null) {
            System.err.println(IAM_MODE_KEY + " not defined in environment");
            System.exit(4);
        }
        if (!Arrays.asList(PROD, TEST, TEST_LOCALHOST, DEV).contains(appMode)) {
            System.err.println("Unknown " + IAM_MODE_KEY + ": " + appMode);
            System.exit(5);
        }
        log.debug("Running in ApplicationMode={}", appMode);
        return appMode;
    }
}
