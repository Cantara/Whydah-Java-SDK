package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.user.types.UserToken;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandUpdateCRMCustomerTest extends BaseCRMCustomerTest{
   

    @Test
    public void testCommandUpdateCRMCustomerTest() throws Exception {
        if (config.isCRMCustomerExtensionSystemTestEnabled()) {
            UserToken adminUserToken = config.logOnSystemTestApplicationAndSystemTestUser();

            //create a dummy customer
            String personJson1 = generateDummyCustomerData("");
            String crmCustomerId = new CommandCreateCRMCustomer(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), null, personJson1).execute();
            
            //update id in the jsondata
            String personJson2 = generateDummyCustomerData(generateUniqueuePersonRef());
            String customerJsonLocation = new CommandUpdateCRMCustomer(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), crmCustomerId, personJson2).execute();
            System.out.println("Returned CRM customer location: " + customerJsonLocation);
            
            
            assertTrue(customerJsonLocation != null);
            assertTrue(customerJsonLocation.endsWith(crmCustomerId));
        }
    }
}
