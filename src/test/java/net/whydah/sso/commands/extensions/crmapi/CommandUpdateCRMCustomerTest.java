package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.application.SystemTestBaseConfig;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandUpdateCRMCustomerTest {
    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }



    @Test
    public void testCommandUpdateCRMCustomerTest() throws Exception {
        if (config.isCRMCustomerExtensionSystemTestEnabled()) {
            UserToken adminUserToken = config.logOnSystemTestApplicationAndSystemTestUser();

            String personRef = "12345678";
            String personJson = "{\"id\":\"12345678\",\"firstname\":\"First\",\"lastname\":\"Lastname\",\"emailaddresses\":null,\"phonenumbers\":null,\"defaultAddressLabel\":null,\"deliveryaddresses\":{\"work\":{\"addressLine1\":\"Karl Johansgate 6\",\"addressLine2\":null,\"postalcode\":\"0160\",\"postalcity\":\"Oslo\"}}}";
            String customerJsonLocation = new CommandUpdateCRMCustomer(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), personRef, personJson).execute();
            System.out.println("Returned CRM customer location: " + customerJsonLocation);
            assertTrue(customerJsonLocation != null);
            assertTrue(customerJsonLocation.endsWith(personRef));
        }
    }
}
