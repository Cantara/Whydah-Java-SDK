package net.whydah.sso.session;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.util.SystemTestBaseConfig;
import net.whydah.sso.util.SystemTestUtil;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

public class WhydahApplicationSessionTest {

    private static final Logger log = getLogger(WhydahApplicationSessionTest.class);
    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }

    @Test
    public void testTimecalculations() throws Exception {
        final int APPLICATION_SESSION_CHECK_INTERVAL_IN_SECONDS = 60;
        DefaultWhydahApplicationSession.ExpireChecker expireChecker = new DefaultWhydahApplicationSession.ExpireChecker(APPLICATION_SESSION_CHECK_INTERVAL_IN_SECONDS);
        log.trace("testTimecalculations() - starting test");
        long i = System.currentTimeMillis() + APPLICATION_SESSION_CHECK_INTERVAL_IN_SECONDS * 3 * 1000 + 200;
        assertFalse(expireChecker.expiresBeforeNextScheduledSessionCheck(i));
        i = System.currentTimeMillis() + APPLICATION_SESSION_CHECK_INTERVAL_IN_SECONDS * 1 * 1000;
        assertTrue(expireChecker.expiresBeforeNextScheduledSessionCheck(i));
        log.trace("testTimecalculations() - done");

    }


    @Test
    //@Ignore
    public void testTimeoutOnLocahost() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {

            WhydahApplicationSession applicationSession = WhydahApplicationSession.getInstance("http://localhost:9998/tokenservice", new ApplicationCredential("15", "MyApp", "33779936R6Jr47D4Hj5R6p9qT"));
            String appToken = applicationSession.getActiveApplicationTokenXML();
            Long expires = ApplicationXpathHelper.getExpiresFromAppTokenXml(applicationSession.getActiveApplicationTokenXML());
            log.debug("Application expires in " + expires + " seconds");
            assertTrue(!WhydahApplicationSession.expiresBeforeNextSchedule(expires));
            log.debug("Thread waiting to expire...  (will take " + expires + " seconds...)");
            Thread.sleep(expires * 1000);
            // Should be marked timeout
            assertTrue(WhydahApplicationSession.expiresBeforeNextSchedule(expires));
            // Session should have been renewed and given a new applicationTokenID
            assertFalse(appToken.equals(applicationSession.getActiveApplicationToken()));
        }
    }

    @Test
    @Ignore
    public void testTimeoutOnSystest() throws Exception {
        if (config.isSystemTestEnabled()) {
            WhydahApplicationSession applicationSession = WhydahApplicationSession.getInstance(config.tokenServiceUri.toString(), config.appCredential);
            WhydahApplicationSession applicationSession2 = WhydahApplicationSession.getInstance(config.tokenServiceUri.toString(), config.appCredential);
            WhydahApplicationSession applicationSession3 = WhydahApplicationSession.getInstance(config.tokenServiceUri.toString(), config.appCredential);
            String appToken = applicationSession.getActiveApplicationTokenXML();
            Long expires = ApplicationXpathHelper.getExpiresFromAppTokenXml(applicationSession.getActiveApplicationTokenXML());
            long waittimeinseconds = (expires - System.currentTimeMillis()) / 1000;
            log.debug("Application Session expires in " + waittimeinseconds + " seconds");
            assertTrue(!WhydahApplicationSession.expiresBeforeNextSchedule(expires));
            log.debug("Thread waiting to expire...  (will take " + waittimeinseconds + " seconds...)");
            Thread.sleep(waittimeinseconds * 4 * 1000);  // Let it run for a while
            // Should be marked timeout
            assertTrue(WhydahApplicationSession.expiresBeforeNextSchedule(expires));
            // Session should have been renewed and given a new applicationTokenID
            assertFalse(appToken.equals(applicationSession.getActiveApplicationToken()));
        }
    }

}
