package net.whydah.sso.commands.adminapi.application;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Test;


public class CommandLogonApplicationTest {
    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
        //config.setLocalTest();
    }

    @Test
    @Ignore
    public void testCommandLogonApplication() throws Exception {
        if (config.isSystemTestEnabled()) {

            ApplicationCredential applicationCredential = new ApplicationCredential("2212", "UserAdminService", "i9ju592A4t8dzz8mz7a5QQJ7Px");
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, applicationCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);

        }
    }
}