package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.application.SystemTestBaseConfig;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandCreateCRMCustomerProfileImageTest {
    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    // TODO  Huy, have a look at this
    @Ignore
    @Test
    public void testCommandCreateCRMCustomerProfileImage() throws Exception {
        if (config.isCRMCustomerExtensionSystemTestEnabled()) {


            UserToken adminUserToken = config.logOnSystemTestApplicationAndSystemTestUser();
            String personRef = "1234567890";
            byte[] data = {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82,
                    0, 0, 0, 15, 0, 0, 0, 15, 8, 6, 0, 0, 0, 59, -42, -107,
                    74, 0, 0, 0, 64, 73, 68, 65, 84, 120, -38, 99, 96, -64, 14, -2,
                    99, -63, 68, 1, 100, -59, -1, -79, -120, 17, -44, -8, 31, -121, 28, 81,
                    26, -1, -29, 113, 13, 78, -51, 100, -125, -1, -108, 24, 64, 86, -24, -30,
                    11, 101, -6, -37, 76, -106, -97, 25, 104, 17, 96, -76, 77, 97, 20, -89,
                    109, -110, 114, 21, 0, -82, -127, 56, -56, 56, 76, -17, -42, 0, 0, 0,
                    0, 73, 69, 78, 68, -82, 66, 96, -126};
            String contenttype = "image/jpeg";

            String imageLocation = new CommandCreateCRMCustomerProfileImage(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), personRef, contenttype, data).execute();
            System.out.println("Returned CRM customer image location: " + imageLocation);
            assertTrue(imageLocation != null);
            assertTrue(imageLocation.endsWith(personRef + "/image"));
        }
    }
}
