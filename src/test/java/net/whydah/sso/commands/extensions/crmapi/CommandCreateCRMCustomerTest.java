package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SSLTool;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static org.junit.Assert.assertTrue;

public class CommandCreateCRMCustomerTest {
    public static String userName = "crmadmin";
    public static String password = "my_secret_password";
    private static URI crmServiceUri;
    private static ApplicationCredential appCredential;
    private static UserCredential userCredential;
    private static boolean systemTest = true;

    @BeforeClass
    public static void setup() throws Exception {
        appCredential = new ApplicationCredential("2299", "MyApp", "33779936R6Jr47D4Hj5R6p9qT");
        crmServiceUri = UriBuilder.fromUri("https:/nohost/crmservice").build();
        userCredential = new UserCredential(userName, password);


        if (systemTest) {
            crmServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/crmservice/").build();
        }
    }


    @Ignore
    @Test
    public void testCreateCRMCustomerCommand() throws Exception {

        String myApplicationTokenID = "";
        String adminUserTokenId = "";
        String personRef = "123456";
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
        SSLTool.disableCertificateValidation();
        String customerJsonLocation = new CommandCreateCRMCustomer(crmServiceUri, myApplicationTokenID, adminUserTokenId, personRef, personJson).execute();
        System.out.println("Returned CRM customer location: " + customerJsonLocation);
        assertTrue(customerJsonLocation != null);
        assertTrue(customerJsonLocation.endsWith(personRef));

    }
}
