package net.whydah.sso.commands.extensions.statistics;

import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SSLTool;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static org.junit.Assert.assertTrue;

/**
 * Created by baardl on 05.03.16.
 */
public class CommandListUserLoginsTest {
    public static String userName = "admin";
    public static String password = "whydahadmin";
    private static URI statisticsServiceUri;
    private static ApplicationCredential appCredential;
    private static UserCredential userCredential;
    private static boolean systemTest = true;

    @BeforeClass
    public static void setup() throws Exception {
        appCredential = new ApplicationCredential("15", "MyApp", "33779936R6Jr47D4Hj5R6p9qT");
        statisticsServiceUri = UriBuilder.fromUri("https://no_host").build();
        userCredential = new UserCredential(userName, password);


        if (systemTest) {
            statisticsServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/reporter/").build();
        }
    }


    @Test
    public void testGetCRMCustomerCommand() throws Exception {

        String myApplicationTokenID = "";
        String adminUserTokenId = "";
        String userid = "me";
        SSLTool.disableCertificateValidation();
        String userLogins = new CommandListUserLogins(statisticsServiceUri, myApplicationTokenID, adminUserTokenId, userid).execute();
        System.out.println("Returned list of userlogins: " + userLogins);
        assertTrue(userLogins != null);
        assertTrue(userLogins.length() > 10);

    }
}