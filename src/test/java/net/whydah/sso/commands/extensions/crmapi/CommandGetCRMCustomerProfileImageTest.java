package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.application.SystemTestBaseConfig;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandGetCRMCustomerProfileImageTest {
    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }

    // TODO  Huy, have a look at this
    @Ignore
    @Test
    public void testCommandGetCRMCustomerProfileImageTest() throws Exception {
        if (config.isCRMCustomerExtensionSystemTestEnabled()) {
            UserToken adminUserToken = config.logOnSystemTestApplicationAndSystemTestUser();

            String personRef = "1234567890";
            byte[] image = new CommandGetCRMCustomerProfileImage(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), personRef).execute();
            System.out.println("Returned CRM customer profile image: " + image);
            assertTrue(image != null);
            assertTrue(image.length > 0);
        }
    }
}
