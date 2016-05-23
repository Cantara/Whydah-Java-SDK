package net.whydah.sso.commands.extras;

import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
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


            ApplicationToken applicationTokenToken = config.logOnSystemTestApplication();
            String myAppTokenXml = ApplicationTokenMapper.toXML(applicationTokenToken);
            long timestamp = new Date().getTime() + 20 * 1000;  // 20 seconds
            assertTrue(new CommandSendScheduledSms(config.tokenServiceUri, config.myApplicationToken.getApplicationTokenId(), myAppTokenXml, Long.toString(timestamp), SystemTestBaseConfig.SYSTEMTEST_USER_CELLPHONE, "Scheduled SMS test").execute());
        }

    }
}
