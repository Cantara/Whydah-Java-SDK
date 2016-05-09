package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.user.types.UserToken;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandCreateCRMCustomerTest extends BaseCRMCustomerTest {

	
    @Test
    public void testCreateCRMCustomerCommand() throws Exception {
        if (config.isCRMCustomerExtensionSystemTestEnabled()) {
            UserToken myUserToken = config.logOnSystemTestApplicationAndSystemTestUser();


            String personRef = generateUniqueuePersonRef(); //Must be unique for test to pass
            String personJson = generateDummyCustomerData(personRef);
            String crmCustomerId = new CommandCreateCRMCustomer(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), myUserToken.getTokenid(), personRef, personJson).execute();
            System.out.println("Returned CRM customer id: " + crmCustomerId);
            assertTrue(crmCustomerId != null);
            assertTrue(crmCustomerId.equals(personRef));
            System.out.println(personJson);
        }

    }

    @Test
    public void testCreateCRMCustomerCommand_NoId() throws Exception {

        if (config.isCRMCustomerExtensionSystemTestEnabled()) {
            UserToken myUserToken = config.logOnSystemTestApplicationAndSystemTestUser();
            String personRef = null;
            String personJson = generateDummyCustomerData("1234");//length should be less than 4 or empty
            String crmCustomerId = new CommandCreateCRMCustomer(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), myUserToken.getTokenid(), personRef, personJson).execute();
            System.out.println("Returned CRM customer id: " + crmCustomerId);
            assertTrue(crmCustomerId != null);
            assertTrue(crmCustomerId.length() > 0);

        }
    }
}
