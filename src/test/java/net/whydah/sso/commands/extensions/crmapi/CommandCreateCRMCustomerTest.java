package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.application.SystemTestBaseConfig;
import net.whydah.sso.user.types.UserToken;

import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.util.Random;

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
        }

    }

    @Test
    public void testCreateCRMCustomerCommand_NoId() throws Exception {

        if (config.isCRMCustomerExtensionSystemTestEnabled()) {
            UserToken myUserToken = config.logOnSystemTestApplicationAndSystemTestUser();
            String personRef = null;
            String personJson = generateDummyCustomerData("123456");
            String crmCustomerId = new CommandCreateCRMCustomer(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), myUserToken.getTokenid(), personRef, personJson).execute();
            System.out.println("Returned CRM customer id: " + crmCustomerId);
            assertTrue(crmCustomerId != null);
            assertTrue(crmCustomerId.length() > 0);

        }
    }
}
