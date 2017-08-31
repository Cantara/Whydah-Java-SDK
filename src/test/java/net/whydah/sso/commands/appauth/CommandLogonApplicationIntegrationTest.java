package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

public class CommandLogonApplicationIntegrationTest {
    private static final Logger log = getLogger(CommandLogonApplicationIntegrationTest.class);
    SystemTestBaseConfig config;
    private String myApplicationTokenID = null;
    private String myAppTokenXml = null;

    @Before
    public void setUp() throws Exception {
        config = new SystemTestBaseConfig();

    }

    @Test
    public void testLogonApplication() throws Exception {
        if (config.isSystemTestEnabled()) {
            myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);

        }
    }
}

