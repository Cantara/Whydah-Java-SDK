package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandValidateApplicationTokenIdTest {


    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testCommandValidateApplicationTokenIdTest() throws Exception {

        if (config.isSystemTestEnabled()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
            assertTrue(myAppTokenXml != null);
            assertTrue(myAppTokenXml.length() > 6);
            ApplicationToken at = ApplicationTokenMapper.fromXml(myAppTokenXml);

            assertTrue(new CommandValidateApplicationTokenId(config.tokenServiceUri.toString(), at.getApplicationTokenId()).execute());
        }
    }

}
