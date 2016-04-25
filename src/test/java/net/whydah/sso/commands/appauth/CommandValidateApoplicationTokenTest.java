package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.SystemTestBaseConfig;
import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.ApplicationToken;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandValidateApoplicationTokenTest {


    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testCommandValidateApoplicationTokenTest() throws Exception {

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
