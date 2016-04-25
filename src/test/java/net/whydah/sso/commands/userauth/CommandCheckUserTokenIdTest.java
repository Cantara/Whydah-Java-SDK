package net.whydah.sso.commands.userauth;

import net.whydah.sso.application.SystemtestBaseConfig;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.user.helpers.UserXpathHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandCheckUserTokenIdTest {

    static SystemtestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemtestBaseConfig();
    }


    @Test
    public void testApplicationLoginCommand() throws Exception {

        if (config.enableTesting()) {
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            System.out.println(myAppTokenXml);
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            System.out.println(myApplicationTokenID);

            assertTrue(myApplicationTokenID.length() > 6);


            String userToken = new CommandLogonUserByUserCredentialWithStubbedFallback(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential).execute();

            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            boolean isvalidToken = new CommandValidateUsertokenIdWithStubbedFallback(config.tokenServiceUri, myApplicationTokenID, userTokenId).execute();
            assertTrue("Error: usertokenid NOT verified successfully", isvalidToken);

        }

    }


}

