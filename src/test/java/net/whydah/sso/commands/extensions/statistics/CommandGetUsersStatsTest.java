package net.whydah.sso.commands.extensions.statistics;

import net.whydah.sso.application.BaseConfig;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SSLTool;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;

import java.net.URI;
import java.time.Instant;

import static org.junit.Assert.assertTrue;

/**
 * Created by baardl on 05.03.16.
 */
public class CommandGetUsersStatsTest {
//    public static String userName = "admin";
//    public static String password = "whydahadmin";
//    private static URI statisticsServiceUri;
//    private static ApplicationCredential appCredential;
//    private static UserCredential userCredential;
//    private static boolean systemTest = true;

	static BaseConfig config;
    @BeforeClass
    public static void setup() throws Exception {
//        appCredential = new ApplicationCredential("15", "MyApp", "33779936R6Jr47D4Hj5R6p9qT");
//        statisticsServiceUri = UriBuilder.fromUri("https://no_host").build();
//        userCredential = new UserCredential(userName, password);

    	config = new BaseConfig();
//        if (systemTest) {
//            statisticsServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/reporter/").build();
//        }
    }

    @Test
    public void testGetUsersStatsCommand() throws Exception {

        String myApplicationTokenID = "";
        String adminUserTokenId = "";
        String userid = "me";
        SSLTool.disableCertificateValidation();
        Instant now = Instant.now();
        Instant lessOneHour = now.minusSeconds(60*60);
        String userStats = new CommandGetUsersStats(config.statisticsServiceUri, myApplicationTokenID, adminUserTokenId, lessOneHour, now).execute();
        System.out.println("Returned list of userlogins: " + userStats);
        assertTrue(userStats != null);
        assertTrue(userStats.length() > 10);

    }
}