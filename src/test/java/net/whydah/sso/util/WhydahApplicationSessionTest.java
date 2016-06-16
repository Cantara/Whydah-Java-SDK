package net.whydah.sso.util;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.user.helpers.UserTokenXpathHelper;

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
        log.trace("testTimecalculations() - starting test");
        long i = System.currentTimeMillis()+200;
        assertTrue(!WhydahApplicationSession.expiresBeforeNextSchedule(i));
        i = System.currentTimeMillis() + 10;
        assertTrue(WhydahApplicationSession.expiresBeforeNextSchedule(i));
        log.trace("testTimecalculations() - done");

    }

   
    @Test
    @Ignore
    public void testTimeoutOnLocahost() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {

            WhydahApplicationSession applicationSession = WhydahApplicationSession.getInstance("http://localhost:9998/tokenservice", "15", "MyApp", "33779936R6Jr47D4Hj5R6p9qT");
            String appToken = applicationSession.getActiveApplicationTokenXML();
            Long expires = ApplicationXpathHelper.getExpiresFromAppTokenXml(applicationSession.getActiveApplicationTokenXML());
            System.out.println("Application expires in " + expires + " seconds");
            assertTrue(!applicationSession.expiresBeforeNextSchedule(expires));
            System.out.println("Thread waiting to expire...  (will take " + expires + " seconds...)");
            Thread.sleep(expires * 1000);
            // Should be marked timeout
            assertTrue(applicationSession.expiresBeforeNextSchedule(expires));
            // Session should have been renewed and given a new applicationTokenID
            assertFalse(appToken.equals(applicationSession.getActiveApplicationToken()));
        }
    }
    
    
}
