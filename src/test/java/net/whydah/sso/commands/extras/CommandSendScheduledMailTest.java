package net.whydah.sso.commands.extras;

import net.whydah.sso.util.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandSendScheduledMailTest {
    static SystemTestBaseConfig config;


    @BeforeClass
    public static void setup() throws Exception {

        config = new SystemTestBaseConfig();

    }


    @Ignore
    @Test
    public void testCommandSendScheduledMailTest() throws Exception {

        if (config.isSystemTestEnabled()) {
            config.logOnSystemTestApplication();

            long timestamp = System.currentTimeMillis() + 50 * 1000;  // 50 seconds
            assertTrue(new CommandSendScheduledMail(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), Long.toString(timestamp), SystemTestBaseConfig.SYSTEMTEST_USER_EMAIL, "whydah-email-passwordlogin-subject", "whydah-email-passwordlogin-body").execute());
            
        }
    }
}
