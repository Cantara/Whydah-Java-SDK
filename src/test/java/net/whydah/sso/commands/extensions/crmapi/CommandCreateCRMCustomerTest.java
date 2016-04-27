package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.application.SystemTestBaseConfig;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertTrue;

public class CommandCreateCRMCustomerTest {
    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    private String getUniqueuePersonRef() {
        Random rand = new Random();
        rand.setSeed(new java.util.Date().getTime());
        String user = "123456" + Integer.toString(rand.nextInt(100000000));
        return user;

    }

    @Test
    public void testCreateCRMCustomerCommand() throws Exception {
        if (config.isCRMCustomerExtensionSystemTestEnabled()) {
            UserToken myUserToken = config.logOnSystemTestApplicationAndSystemTestUser();


            String personRef = getUniqueuePersonRef(); //Must be unique for test to pass
            String personJson = "{\n" +
                    "  \"id\" : \"" + personRef + "\",\n" +
                    "  \"firstname\" : \"First\",\n" +
                    "  \"lastname\" : \"Lastname\",\n" +
                    "  \"emailaddresses\" : {\n" +
                    "    \"jobb\" : {\n" +
                    "      \"emailaddress\" : \"totto@capraconsulting.no\",\n" +
                    "      \"tags\" : \"jobb, Capra\"\n" +
                    "    },\n" +
                    "    \"kobb-kunde\" : {\n" +
                    "      \"emailaddress\" : \"thor.henning.hetland@nmd.no\",\n" +
                    "      \"tags\" : \"jobb, kunde\"\n" +
                    "    },\n" +
                    "    \"community\" : {\n" +
                    "      \"emailaddress\" : \"totto@cantara.no\",\n" +
                    "      \"tags\" : \"opensource, privat, Whydah\"\n" +
                    "    },\n" +
                    "    \"hjemme\" : {\n" +
                    "      \"emailaddress\" : \"totto@totto.org\",\n" +
                    "      \"tags\" : \"hjemme, privat, OID\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"phonenumbers\" : {\n" +
                    "    \"tja\" : {\n" +
                    "      \"phonenumber\" : \"privat\",\n" +
                    "      \"tags\" : \"96909999\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"defaultAddressLabel\" : null,\n" +
                    "  \"deliveryaddresses\" : {\n" +
                    "    \"work, override\" : {\n" +
                    "      \"addressLine1\" : \"Stenersgata 2\",\n" +
                    "      \"addressLine2\" : null,\n" +
                    "      \"postalcode\" : \"0184\",\n" +
                    "      \"postalcity\" : \"Oslo\"\n" +
                    "    },\n" +
                    "    \"home\" : {\n" +
                    "      \"addressLine1\" : \"Møllefaret 30E\",\n" +
                    "      \"addressLine2\" : null,\n" +
                    "      \"postalcode\" : \"0750\",\n" +
                    "      \"postalcity\" : \"Oslo\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            String crmCustomerId = new CommandCreateCRMCustomer(config.crmServiceUri, config.myApplicationToken.getApplicationID(), myUserToken.getTokenid(), personRef, personJson).execute();
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
            String personJson = "{\n" +
                    "  \"id\" : \"123456\",\n" +
                    "  \"firstname\" : \"First\",\n" +
                    "  \"lastname\" : \"Lastname\",\n" +
                    "  \"emailaddresses\" : {\n" +
                    "    \"jobb\" : {\n" +
                    "      \"emailaddress\" : \"totto@capraconsulting.no\",\n" +
                    "      \"tags\" : \"jobb, Capra\"\n" +
                    "    },\n" +
                    "    \"kobb-kunde\" : {\n" +
                    "      \"emailaddress\" : \"thor.henning.hetland@nmd.no\",\n" +
                    "      \"tags\" : \"jobb, kunde\"\n" +
                    "    },\n" +
                    "    \"community\" : {\n" +
                    "      \"emailaddress\" : \"totto@cantara.no\",\n" +
                    "      \"tags\" : \"opensource, privat, Whydah\"\n" +
                    "    },\n" +
                    "    \"hjemme\" : {\n" +
                    "      \"emailaddress\" : \"totto@tott.org\",\n" +
                    "      \"tags\" : \"hjemme, privat, OID\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"phonenumbers\" : {\n" +
                    "    \"tja\" : {\n" +
                    "      \"phonenumber\" : \"privat\",\n" +
                    "      \"tags\" : \"96909999\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"defaultAddressLabel\" : null,\n" +
                    "  \"deliveryaddresses\" : {\n" +
                    "    \"work, override\" : {\n" +
                    "      \"addressLine1\" : \"Stenersgata 2\",\n" +
                    "      \"addressLine2\" : null,\n" +
                    "      \"postalcode\" : \"0184\",\n" +
                    "      \"postalcity\" : \"Oslo\"\n" +
                    "    },\n" +
                    "    \"home\" : {\n" +
                    "      \"addressLine1\" : \"Møllefaret 30E\",\n" +
                    "      \"addressLine2\" : null,\n" +
                    "      \"postalcode\" : \"0750\",\n" +
                    "      \"postalcity\" : \"Oslo\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            String crmCustomerId = new CommandCreateCRMCustomer(config.crmServiceUri, config.myApplicationToken.getApplicationID(), myUserToken.getTokenid(), personRef, personJson).execute();
            System.out.println("Returned CRM customer id: " + crmCustomerId);
            assertTrue(crmCustomerId != null);
            assertTrue(crmCustomerId.length() > 0);

        }
    }
}
