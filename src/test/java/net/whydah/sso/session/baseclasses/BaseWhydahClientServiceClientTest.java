package net.whydah.sso.session.baseclasses;

import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BaseWhydahClientServiceClientTest {

    static SystemTestBaseConfig config;
    static BaseWhydahServiceClient client;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
        if (config.isSystemTestEnabled()) {

            client = new BaseWhydahServiceClient(config.tokenServiceUri.toString(), config.userAdminServiceUri.toString(), config.TEMPORARY_APPLICATION_ID, config.TEMPORARY_APPLICATION_NAME, config.TEMPORARY_APPLICATION_SECRET);
        }
    }

    @Test
    public void testUpdateRoleAndRefreshUserToken(){
        if (config.isSystemTestEnabled()) {

            assertTrue(client.updateOrCreateUserApplicationRoleEntry("", "ACSResource", "WhyDah", "INNData", "welcome", config.logOnSystemTestApplicationAndSystemTestUser_getTokenXML()));
        }
    }
}
