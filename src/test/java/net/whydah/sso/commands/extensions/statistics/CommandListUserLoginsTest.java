package net.whydah.sso.commands.extensions.statistics;

import net.whydah.sso.application.SystemTestBaseConfig;
import net.whydah.sso.util.SSLTool;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertTrue;

/**
 * Created by baardl on 05.03.16.
 */
public class CommandListUserLoginsTest {
    //private static ApplicationCredential appCredential;
    //private static UserCredential userCredential;
    //private static boolean systemTest = false;
    static SystemTestBaseConfig config;
    //public static String userName = "admin";
    //public static String password = "whydahadmin";
    private static URI statisticsServiceUri;
    
    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
        //appCredential = new ApplicationCredential("15", "MyApp", "33779936R6Jr47D4Hj5R6p9qT");
        //statisticsServiceUri = URI.create("https://no_host").build();
        //userCredential = new UserCredential(userName, password);


//        if (systemTest) {
//            statisticsServiceUri = URI.create("https://whydahdev.cantara.no/reporter/").build();
//        }
    }


    @Ignore
    @Test
    public void testUserLoginsCustomerCommand() throws Exception {

        String myApplicationTokenID = "";
        String adminUserTokenId = "";
        String userid = "useradmin";
        SSLTool.disableCertificateValidation();
        String userLogins = new CommandListUserLogins(statisticsServiceUri, myApplicationTokenID, adminUserTokenId, userid).execute();
        System.out.println("Returned list of userlogins: " + userLogins);
        assertTrue(userLogins != null);
        assertTrue(userLogins.length() > 10);

    }
}