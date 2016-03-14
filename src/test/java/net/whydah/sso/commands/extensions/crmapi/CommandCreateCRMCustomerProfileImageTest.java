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

public class CommandCreateCRMCustomerProfileImageTest {
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


    @Test
    public void testCreateCRMCustomerProfileImageCommand() throws Exception {

        String myApplicationTokenID = "";
        String adminUserTokenId = "";
        String personRef = "123456";
        byte[] data = new byte[256];
        String contenttype = "image/jpeg";

        SSLTool.disableCertificateValidation();
        String imageLocation = new CommandCreateCRMCustomerProfileImage(crmServiceUri, myApplicationTokenID, adminUserTokenId, personRef, contenttype, data).execute();
        System.out.println("Returned CRM customer image location: " + imageLocation);
        assertTrue(imageLocation != null);
        assertTrue(imageLocation.endsWith(personRef + "/image"));
    }
}
