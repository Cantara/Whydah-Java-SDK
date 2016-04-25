package net.whydah.sso.commands.extensions.crmapi;

import static org.junit.Assert.assertTrue;

import java.net.URI;

import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SSLTool;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class CommandGetCRMCustomerTest {
    public static String userName = "admin";
    public static String password = "whydahadmin";
    private static URI crmServiceUri;
    private static ApplicationCredential appCredential;
    private static UserCredential userCredential;
    private static boolean systemTest = true;

    @BeforeClass
    public static void setup() throws Exception {
        appCredential = new ApplicationCredential("15", "MyApp", "33779936R6Jr47D4Hj5R6p9qT");
        crmServiceUri = URI.create("https://no_host");
        userCredential = new UserCredential(userName, password);


        if (systemTest) {
            crmServiceUri = URI.create("https://whydahdev.cantara.no/crmservice/");
        }
    }


    @Test
    @Ignore
    public void testGetCRMCustomerCommand() throws Exception {

        String myApplicationTokenID = "dummyAppTokenId";
        String adminUserTokenId = "dummyAdminUserToken";

        String personRef = "1234567890";
        SSLTool.disableCertificateValidation();
        String customerJson = new CommandGetCRMCustomer(crmServiceUri, myApplicationTokenID, adminUserTokenId, personRef).execute();
        System.out.println("Returned CRM customer: " + customerJson);
        assertTrue(customerJson != null);
        assertTrue(customerJson.length() > 10);

    }
}
