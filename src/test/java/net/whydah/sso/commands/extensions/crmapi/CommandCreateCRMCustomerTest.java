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
    public static String userName = "admin";
    public static String password = "whydahadmin";
    private static URI crmServiceUri;
    private static ApplicationCredential appCredential;
    private static UserCredential userCredential;
    private static boolean systemTest = true;

    @BeforeClass
    public static void setup() throws Exception {
        appCredential = new ApplicationCredential("15", "MyApp", "33779936R6Jr47D4Hj5R6p9qT");
        crmServiceUri = UriBuilder.fromUri("https://no_host").build();
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
        String personRef = "12345678";
        String personJson = "{\"id\":\"12345678\",\"firstname\":\"First\",\"lastname\":\"Lastname\",\"emailaddresses\":null,\"phonenumbers\":null,\"defaultAddressLabel\":null,\"deliveryaddresses\":{\"work\":{\"addressLine1\":\"Karl Johansgate 6\",\"addressLine2\":null,\"postalcode\":\"0160\",\"postalcity\":\"Oslo\"}}}";
        SSLTool.disableCertificateValidation();
        String customerJsonLocation = new CommandCreateCRMCustomer(crmServiceUri, myApplicationTokenID, adminUserTokenId, personRef, personJson).execute();
        System.out.println("Returned CRM customer location: " + customerJsonLocation);
        assertTrue(customerJsonLocation != null);
        assertTrue(customerJsonLocation.endsWith(personRef));

    }
}
