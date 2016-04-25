package net.whydah.sso.commands.extensions.statistics;

import static org.junit.Assert.assertTrue;

import java.net.URI;

import net.whydah.sso.application.BaseConfig;
import net.whydah.sso.util.SSLTool;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by baardl on 05.03.16.
 */
public class CommandListUserLoginsTest {
    //public static String userName = "admin";
    //public static String password = "whydahadmin";
    private static URI statisticsServiceUri;
    //private static ApplicationCredential appCredential;
    //private static UserCredential userCredential;
    //private static boolean systemTest = false;
    static BaseConfig config;
    
    @BeforeClass
    public static void setup() throws Exception {
    	config = new BaseConfig();
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