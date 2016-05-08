package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.extensions.crmcustomer.mappers.CustomerMapper;
import net.whydah.sso.user.types.UserToken;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class CommandGetCRMCustomerTest extends BaseCRMCustomerTest {

    private static final Logger log = LoggerFactory.getLogger(CommandGetCRMCustomerTest.class);


    @Test
    public void testGetCRMCustomerCommand() throws Exception {
        if (config.isCRMCustomerExtensionSystemTestEnabled()) {


            UserToken adminUserToken = config.logOnSystemTestApplicationAndSystemTestUser();
            
            //create dummy customer
            String personJson = generateDummyCustomerData(generateUniqueuePersonRef());
            String crmCustomerId = new CommandCreateCRMCustomer(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), null, personJson).execute();
            
            //query
            String customerJson = new CommandGetCRMCustomer(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), crmCustomerId).execute();
            log.debug("Returned CRM id: {} - crmCustomerId {} - customer: {}", CustomerMapper.fromJson(customerJson).getId(), crmCustomerId, customerJson);
            assertTrue(customerJson != null);
            assertTrue(customerJson.length() > 10);
            assertTrue(crmCustomerId.equalsIgnoreCase(CustomerMapper.fromJson(customerJson).getId()));
        }

    }
}
