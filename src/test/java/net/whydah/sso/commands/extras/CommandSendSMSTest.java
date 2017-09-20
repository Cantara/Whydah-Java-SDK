package net.whydah.sso.commands.extras;

import static junit.framework.TestCase.assertTrue;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandGenerateAndSendSmsPin;
import net.whydah.sso.util.SystemTestBaseConfig;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommandSendSMSTest {

    private static final Logger log = LoggerFactory.getLogger(CommandSendSMSTest.class);
    public static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testSendSMSPin() throws Exception {

        if (config.isSystemTestEnabled()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            //     public CommandGenerateAndSendSmsPin(URI tokenServiceUri, String appTokenId, String appTokenXml, String phoneNo) {
            assertTrue(new CommandGenerateAndSendSmsPin(config.tokenServiceUri, config.myApplicationTokenID, myAppTokenXml, SystemTestBaseConfig.SYSTEMTEST_USER_CELLPHONE).execute());
        }

    }

}
