package net.whydah.sso.util;

import net.whydah.sso.user.UserRole;
import net.whydah.sso.user.UserRoleXPathHelper;
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
        assertTrue(!WhydahApplicationSession.expiresBeforeNextSchedule(Long.toString(i)));
         i = System.currentTimeMillis()+30;
        assertTrue(WhydahApplicationSession.expiresBeforeNextSchedule(Long.toString(i)));

    }
}
