package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.user.types.UserToken;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class CommandGetCRMCustomerProfileImageTest extends BaseCRMCustomerTest {
    
   
    @Test
    public void testCommandGetCRMCustomerProfileImageTest() throws Exception {
        if (config.isCRMCustomerExtensionSystemTestEnabled()) {
            UserToken adminUserToken = config.logOnSystemTestApplicationAndSystemTestUser();
            //create dummy customer
            String personJson = generateDummyCustomerData("");
            String crmCustomerId = new CommandCreateCRMCustomer(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), null, personJson).execute();
            //create some dummy image with [crmCustomerId]
            byte[] image = generateDummyCustomerPhoto();
            String imageLocation = new CommandCreateCRMCustomerProfileImage(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), crmCustomerId, contenttype, image).execute();
            //query customer image
            byte[] image2 = new CommandGetCRMCustomerProfileImage(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), crmCustomerId).execute();
            System.out.println("Returned CRM customer profile image: " + image);
            assertTrue(image != null);
            assertTrue(image.length > 0);
            assertTrue(Arrays.equals(image, image2));
        }
    }
}
