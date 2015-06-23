package net.whydah.sso.util;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by totto on 23.06.15.
 */
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
    public void testTimeoutOnLocahost() throws Exception{
        WhydahApplicationSession applicationSession = new WhydahApplicationSession("http://localhost:9998/tokenservice","15","33779936R6Jr47D4Hj5R6p9qT");
        long i = System.currentTimeMillis()+200;
        assertTrue(!applicationSession.expiresBeforeNextSchedule(i));
        i = System.currentTimeMillis()+30;
        assertTrue(applicationSession.expiresBeforeNextSchedule(i));
    }
}
