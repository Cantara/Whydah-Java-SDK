package net.whydah.sso.commands.userauth;

import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class CommandLogonUserByPinTest {
    static SystemTestBaseConfig config;
    


    @BeforeClass
    public static void setup() throws Exception {

        config = new SystemTestBaseConfig();

    }

    

    @Test
    public void testCommandLogonUserByPinTest() throws Exception {

        if (config.isSystemTestEnabled()) {
            UserToken adminUserToken = config.logOnSystemTestApplicationAndSystemTestUser();
            String myAppTokenXml = ApplicationTokenMapper.toXML(config.myApplicationToken);
            String phoneNo = "98765432";
            String pin = config.generatePin();
            String ticket = "734985984325";
            new CommandSendSmsPin(config.tokenServiceUri, config.myApplicationToken.getApplicationTokenId(), myAppTokenXml, phoneNo, pin).execute();
            String userTokenXML = new CommandLogonUserByPhoneNumberPin(config.tokenServiceUri, config.myApplicationToken.getApplicationTokenId(), myAppTokenXml, adminUserToken.getTokenid(), phoneNo, pin, ticket).execute();
            assertNotNull(userTokenXML);
            assertTrue(userTokenXML.contains(phoneNo));

        }
    }
}
