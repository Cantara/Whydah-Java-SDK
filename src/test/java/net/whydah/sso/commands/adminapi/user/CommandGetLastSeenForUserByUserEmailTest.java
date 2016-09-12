package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandGetLastSeenForUserByUserEmailTest {

    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testCommandGetLastSeenForUserByUserEmail() throws Exception {

        if (config.isSystemTestEnabled()) {
            String myAppTokenXml;
            myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();

            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
            String lastSeen = new CommandGetLastSeenForUserByUserEmail(config.tokenServiceUri, myApplicationTokenID, config.SYSTEMTEST_USER_EMAIL).execute();
            // Need to create real testdata for the assert to work, as systemusers does not have the roles set..
            assertTrue(lastSeen != null);
            assertTrue(lastSeen.length() > 4);


        }
    }

}
