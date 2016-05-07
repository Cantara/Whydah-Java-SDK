package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.user.types.UserToken;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandCreateCRMCustomerProfileImageTest extends BaseCRMCustomerTest{
   
    @Test
    public void testCommandCreateCRMCustomerProfileImage() throws Exception {
        if (config.isCRMCustomerExtensionSystemTestEnabled()) {


            UserToken adminUserToken = config.logOnSystemTestApplicationAndSystemTestUser();
            String personRef = generateUniqueuePersonRef();
           
            String imageLocation = new CommandCreateCRMCustomerProfileImage(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), personRef, contenttype, generateDummyCustomerPhoto()).execute();
            System.out.println("Returned CRM customer image location: " + imageLocation);
            assertTrue(imageLocation != null);
            assertTrue(imageLocation.endsWith(personRef + "/image"));
        }
    }
}
