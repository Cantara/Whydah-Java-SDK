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

public class CommandUpdateCRMCustomerProfileImageTest {
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
    public void testUpdateCRMCustomerProfileImageCommand() throws Exception {

        String myApplicationTokenID = "dummyAppTokenId";
        String adminUserTokenId = "dummyAdminUserToken";
        String personRef = "1234567890";
        byte[] data = {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82,
                0, 0, 0, 15, 0, 0, 0, 15, 8, 6, 0, 0, 0, 59, -42, -107,
                74, 0, 0, 0, 64, 73, 68, 65, 84, 120, -38, 99, 96, -64, 14, -2,
                99, -63, 68, 1, 100, -59, -1, -79, -120, 17, -44, -8, 31, -121, 28, 81,
                26, -1, -29, 113, 13, 78, -51, 100, -125, -1, -108, 24, 64, 86, -24, -30,
                11, 101, -6, -37, 76, -106, -97, 25, 104, 17, 96, -76, 77, 97, 20, -89,
                109, -110, 114, 21, 0, -82, -127, 56, -56, 56, 76, -17, -42, 0, 0, 0,
                0, 73, 69, 78, 68, -82, 66, 96, -126};
        String contenttype = "image/jpeg";

        SSLTool.disableCertificateValidation();
        String customerJsonLocation = new CommandUpdateCRMCustomerProfileImage(crmServiceUri, myApplicationTokenID, adminUserTokenId, personRef, contenttype, data).execute();
        System.out.println("Returned CRM profileImage location: " + customerJsonLocation);
        assertTrue(customerJsonLocation != null);
        assertTrue(customerJsonLocation.endsWith(personRef + "/image"));

    }
}
