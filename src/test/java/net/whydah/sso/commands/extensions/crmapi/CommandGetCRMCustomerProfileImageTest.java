package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SSLTool;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.awt.image.BufferedImage;
import java.net.URI;

import static org.junit.Assert.assertTrue;

public class CommandGetCRMCustomerProfileImageTest {
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
    public void testGetCRMCustomerProfileImageCommand() throws Exception {

        String myApplicationTokenID = "dummyAppTokenId";
        String adminUserTokenId = "dummyAdminUserToken";
        String personRef = "1234567890";
        SSLTool.disableCertificateValidation();
        byte[] image = new CommandGetCRMCustomerProfileImage(crmServiceUri, myApplicationTokenID, adminUserTokenId, personRef).execute();
        System.out.println("Returned CRM customer profile image: " + image);
        assertTrue(image != null);
        assertTrue(image.length > 0);
    }
}
