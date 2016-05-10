package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.util.SSLTool;
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
            SSLTool.disableCertificateValidation();
            ApplicationCredential appCredential = new ApplicationCredential(config.TEMPORARY_APPLICATION_ID, config.TEMPORARY_APPLICATION_NAME, config.TEMPORARY_APPLICATION_SECRET);
            myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, appCredential).execute();
            myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);

        }
    }
}

