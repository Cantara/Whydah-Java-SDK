package net.whydah.sso.commands.extras;

import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class CommandSendScheduledSMSTest {
    static SystemTestBaseConfig config;


    @BeforeClass
    public static void setup() throws Exception {

        config = new SystemTestBaseConfig();

    }


    @Test
    public void testCommandSendScheduledSMSTest() throws Exception {

        if (config.isSystemTestEnabled()) {
            UserToken adminUserToken = config.logOnSystemTestApplicationAndSystemTestUser();
            String myAppTokenXml = ApplicationTokenMapper.toXML(config.myApplicationToken);
            long timestamp = new Date().getTime() + 20 * 1000;  // 20 seconds
            assertTrue(new CommandSendScheduledSms(config.tokenServiceUri, config.myApplicationToken.getApplicationTokenId(), myAppTokenXml, Long.toString(timestamp), "91905054", "Scheduled SMS test").execute());

        }
    }
}
