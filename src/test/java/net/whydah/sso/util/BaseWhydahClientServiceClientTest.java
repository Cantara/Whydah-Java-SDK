package net.whydah.sso.util;

import net.whydah.sso.session.baseclasses.BaseWhydahServiceClient;
import org.junit.BeforeClass;

public class BaseWhydahClientServiceClientTest {

    static SystemTestBaseConfig config;
    static BaseWhydahServiceClient client;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
        if (config.isSystemTestEnabled()) {

            client = new BaseWhydahServiceClient(config.tokenServiceUri.toString(), config.userAdminServiceUri.toString(), config.appCredential);
        }
    }

}
