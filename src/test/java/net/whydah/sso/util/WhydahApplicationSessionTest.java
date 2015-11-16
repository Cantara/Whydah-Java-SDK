package net.whydah.sso.util;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.session.WhydahApplicationSession;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

public class WhydahApplicationSessionTest {

    private static final Logger log = getLogger(WhydahApplicationSessionTest.class);

    @Test
    public void testTimecalculations() throws Exception {
        long i = System.currentTimeMillis()+200;
        assertTrue(!WhydahApplicationSession.expiresBeforeNextSchedule(i));
         i = System.currentTimeMillis()+30;
        assertTrue(WhydahApplicationSession.expiresBeforeNextSchedule(i));

    }

    @Ignore
    @Test
    public void testTimeoutOnLocahost() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {

            WhydahApplicationSession applicationSession = new WhydahApplicationSession("http://localhost:9998/tokenservice", "15", "33779936R6Jr47D4Hj5R6p9qT");
            String appToken = applicationSession.getActiveApplicationToken();
            Long expires = ApplicationXpathHelper.getExpiresFromAppTokenXml(applicationSession.getActiveApplicationToken());
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
