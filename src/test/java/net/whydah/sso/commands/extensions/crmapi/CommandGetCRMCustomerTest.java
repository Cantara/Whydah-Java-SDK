package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.user.types.UserToken;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandGetCRMCustomerTest extends BaseCRMCustomerTest {
  
    @Test
    public void testGetCRMCustomerCommand() throws Exception {
        if (config.isCRMCustomerExtensionSystemTestEnabled()) {


            UserToken adminUserToken = config.logOnSystemTestApplicationAndSystemTestUser();
            
            //create dummy customer
            String personJson = generateDummyCustomerData("123456");
            String crmCustomerId = new CommandCreateCRMCustomer(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), null, personJson).execute();
            
            //query
            String customerJson = new CommandGetCRMCustomer(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), crmCustomerId).execute();
            System.out.println("Returned CRM customer: " + customerJson);
            assertTrue(customerJson != null);
            assertTrue(customerJson.length() > 10);
        }

    }
}
