package net.whydah.sso.session;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

public class WhydahApplicationSessionInitializationTest {

    private static final Logger log = getLogger(WhydahApplicationSessionInitializationTest.class);
    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    @Ignore
    public void testTimeoutOnSystest() throws Exception {
        if (config.isSystemTestEnabled()) {

            int threads = 20;
            ExecutorService executorService = Executors.newFixedThreadPool(threads);

            for (int n = 0; n < threads; n++) {
                executorService.execute(new Runnable() {
                    public void run() {
                        startWAS();
                        log.debug("Asynchronous startWhydahClient task: ");
                    }
                });

            }
            Thread.sleep(10000);
            WhydahApplicationSession applicationSession = WhydahApplicationSession.getInstance(config.tokenServiceUri.toString(), config.appCredential);

            String appToken = applicationSession.getActiveApplicationTokenXML();
            Long expires = ApplicationXpathHelper.getExpiresFromAppTokenXml(applicationSession.getActiveApplicationTokenXML());
            long waittimeinseconds = (expires - System.currentTimeMillis()) / 1000;
            log.debug("Application Session expires in " + waittimeinseconds + " seconds");
            assertTrue(!applicationSession.expiresBeforeNextSchedule(expires));
            log.debug("Thread waiting to expire...  (will take " + waittimeinseconds + " seconds...)");
            Thread.sleep(waittimeinseconds * 4 * 1000);  // Let it run for a while
            // Should be marked timeout
            assertTrue(applicationSession.expiresBeforeNextSchedule(expires));
            // Session should have been renewed and given a new applicationTokenID
            assertFalse(appToken.equals(applicationSession.getActiveApplicationToken()));
        }
    }

    private void startWAS() {
        WhydahApplicationSession applicationSession = WhydahApplicationSession.getInstance(config.tokenServiceUri.toString(), config.appCredential);
        String appToken = applicationSession.getActiveApplicationTokenXML();
        Long expires = ApplicationXpathHelper.getExpiresFromAppTokenXml(applicationSession.getActiveApplicationTokenXML());
        long waittimeinseconds = (expires - System.currentTimeMillis()) / 1000;
        log.debug("Application Session expires in " + waittimeinseconds + " seconds");
        log.debug("Thread waiting to expire...  (will take " + waittimeinseconds + " seconds...)");

    }

}
