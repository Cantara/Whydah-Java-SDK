package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.user.types.UserToken;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandUpdateCRMCustomerProfileImageTest extends BaseCRMCustomerTest {

   
    @Test
    public void testUpdateCRMCustomerProfileImageCommand() throws Exception {
    	if(config.isCRMCustomerExtensionSystemTestEnabled()){
    		
    		UserToken adminUserToken = config.logOnSystemTestApplicationAndSystemTestUser();
    		//create dummy customer
            String personJson = generateDummyCustomerData("123456");
            String crmCustomerId = new CommandCreateCRMCustomer(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), null, personJson).execute();
            
           //create some dummy image with [crmCustomerId]
            byte[] image1 = generateDummyCustomerPhoto();
            String imageLocation = new CommandCreateCRMCustomerProfileImage(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), crmCustomerId, contenttype, image1).execute();
    		
    		byte[] image2 = generateDummyCustomerPhoto();
    		String customerJsonLocation = new CommandUpdateCRMCustomerProfileImage(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), crmCustomerId, contenttype, image2).execute();
    		
    		
    		System.out.println("Returned CRM profileImage location: " + customerJsonLocation);
    		assertTrue(customerJsonLocation != null);
    		assertTrue(customerJsonLocation.endsWith(crmCustomerId + "/image"));
    	}

    }
}
