package net.whydah.sso.commands.extras;

import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

public class CommandSendScheduledMailTest {
    static SystemTestBaseConfig config;


    @BeforeClass
    public static void setup() throws Exception {

        config = new SystemTestBaseConfig();

    }


    @Test
    public void testCommandSendScheduledMailTest() throws Exception {

        if (config.isSystemTestEnabled()) {
            UserToken adminUserToken = config.logOnSystemTestApplicationAndSystemTestUser();
            String myAppTokenXml = ApplicationTokenMapper.toXML(config.myApplicationToken);

            long timestamp = new Date().getTime() + 50 * 1000;  // 50 seconds
            new CommandSendScheduledMail(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), myAppTokenXml, Long.toString(timestamp), "totto@totto.org", "inn-email-passwordlogin-subject", "inn-email-passwordlogin-body");
        }
    }
}
