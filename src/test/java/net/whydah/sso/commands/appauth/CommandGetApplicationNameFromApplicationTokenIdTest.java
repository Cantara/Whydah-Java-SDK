package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandGetApplicationNameFromApplicationTokenIdTest {

    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testCommandGetApplicationNameFromApplicationTokenId() throws Exception {
        if (config.isSystemTestEnabled()) {
            
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
            String applicationID = new CommandGetApplicationIdFromApplicationTokenId(config.tokenServiceUri, myApplicationTokenID).execute();

            System.out.println("Found applicationID: {}" + applicationID);

        }
    }
}
