package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.application.SystemTestBaseConfig;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.util.SSLTool;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

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
            String myApplicationTokenID = "";
            SSLTool.disableCertificateValidation();
            ApplicationCredential appCredential = new ApplicationCredential(config.TEMPORARY_APPLICATION_ID, config.TEMPORARY_APPLICATION_NAME, config.TEMPORARY_APPLICATION_SECRET);
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, appCredential).execute();
            myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue("Unable to log on application ", myApplicationTokenID.length() > 10);

            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            assertTrue("Unable to logon user ", userTokenId.length() > 10);


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
            String crmCustomerId = new CommandCreateCRMCustomer(config.crmServiceUri, myApplicationTokenID, userTokenId, personRef, personJson).execute();
            System.out.println("Returned CRM customer id: " + crmCustomerId);
            assertTrue(crmCustomerId != null);
            assertTrue(crmCustomerId.equals(personRef));
        }

    }

    @Test
    public void testCreateCRMCustomerCommand_NoId() throws Exception {

        if (config.isCRMCustomerExtensionSystemTestEnabled()) {
            String myApplicationTokenID = "";
            SSLTool.disableCertificateValidation();
            ApplicationCredential appCredential = new ApplicationCredential(config.TEMPORARY_APPLICATION_ID, config.TEMPORARY_APPLICATION_NAME, config.TEMPORARY_APPLICATION_SECRET);
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, appCredential).execute();
            myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue("Unable to log on application ", myApplicationTokenID.length() > 10);

            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            assertTrue("Unable to log on user", userTokenId.length() > 10);
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
            String crmCustomerId = new CommandCreateCRMCustomer(config.crmServiceUri, myApplicationTokenID, userTokenId, personRef, personJson).execute();
            System.out.println("Returned CRM customer id: " + crmCustomerId);
            assertTrue(crmCustomerId != null);
            assertTrue(crmCustomerId.length() > 0);

        }
    }
}
