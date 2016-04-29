package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.application.SystemTestBaseConfig;
import net.whydah.sso.user.types.UserToken;
import net.whydah.sso.util.SSLTool;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandGetCRMCustomerTest {
    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }



    @Test
    public void testGetCRMCustomerCommand() throws Exception {
        if (config.isCRMCustomerExtensionSystemTestEnabled()) {


            UserToken adminUserToken = config.logOnSystemTestApplicationAndSystemTestUser();
            String personRef = "1234567890";
            SSLTool.disableCertificateValidation();
            String customerJson = new CommandGetCRMCustomer(config.crmServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUserToken.getTokenid(), personRef).execute();
            System.out.println("Returned CRM customer: " + customerJson);
            assertTrue(customerJson != null);
            assertTrue(customerJson.length() > 10);
        }

    }
}
