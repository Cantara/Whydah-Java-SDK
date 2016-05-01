package net.whydah.sso.commands.userauth;

import net.whydah.sso.application.SystemTestBaseConfig;
import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Test;

public class CommandLogonUserByPinTest {
    static SystemTestBaseConfig config;
    private static java.util.Random generator = new java.util.Random();


    @BeforeClass
    public static void setup() throws Exception {

        config = new SystemTestBaseConfig();

    }

    public static String generatePin() {
        generator.setSeed(System.currentTimeMillis());
        int i = generator.nextInt(10000) % 10000;

        java.text.DecimalFormat f = new java.text.DecimalFormat("0000");
        return f.format(i);

    }

    @Test
    public void testCCommandLogonUserByPinTest() throws Exception {

        if (config.isSystemTestEnabled()) {
            UserToken adminUserToken = config.logOnSystemTestApplicationAndSystemTestUser();
            String myAppTokenXml = ApplicationTokenMapper.toXML(config.myApplicationToken);
            String phoneNo = "12345678";
            String pin = generatePin();
            String ticket = "734985984325";
            new CommandSendSmsPin(config.tokenServiceUri, config.myApplicationToken.getApplicationTokenId(), myAppTokenXml, phoneNo, pin).execute();
            String userTokenXML = new CommandLogonUserByPhoneNumberPin(config.tokenServiceUri, config.myApplicationToken.getApplicationTokenId(), myAppTokenXml, phoneNo, pin, ticket).execute();

        }
    }
}
