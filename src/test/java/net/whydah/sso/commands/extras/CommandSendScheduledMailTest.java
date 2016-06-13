package net.whydah.sso.commands.extras;

import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class CommandSendScheduledMailTest {
    static SystemTestBaseConfig config;


    @BeforeClass
    public static void setup() throws Exception {

        config = new SystemTestBaseConfig();

    }


    @Test
    public void testCommandSendScheduledMailTest() throws Exception {

        if (config.isSystemTestEnabled()) {
            ApplicationToken applicationTokenToken = config.logOnSystemTestApplication();
            String myAppTokenXml = ApplicationTokenMapper.toXML(applicationTokenToken);

            long timestamp = new Date().getTime() + 50 * 1000;  // 50 seconds
            assertTrue(new CommandSendScheduledMail(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), myAppTokenXml, Long.toString(timestamp), SystemTestBaseConfig.SYSTEMTEST_USER_EMAIL, "whydah-email-passwordlogin-subject", "whydah-email-passwordlogin-body").execute());
        }
    }
}
