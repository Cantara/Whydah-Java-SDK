package net.whydah.sso.commands.extensions.statistics;

import net.whydah.sso.application.BaseConfig;
import net.whydah.sso.util.SSLTool;
import org.junit.BeforeClass;
import org.junit.Test;

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
//        statisticsServiceUri = URI.create("https://no_host").build();
//        userCredential = new UserCredential(userName, password);

        config = new BaseConfig();
//        if (systemTest) {
//            statisticsServiceUri = URI.create("https://whydahdev.cantara.no/reporter/").build();
//        }
    }

    @Test
    public void testGetUsersStatsCommand() throws Exception {

        if (config.enableTesting()) {

            String myApplicationTokenID = "";
            String adminUserTokenId = "";
            String userid = "me";
            SSLTool.disableCertificateValidation();
            Instant now = Instant.now();
            Instant lessOneHour = now.minusSeconds(60 * 60);
            String userStats = new CommandGetUsersStats(config.statisticsServiceUri, myApplicationTokenID, adminUserTokenId, lessOneHour, now).execute();
            System.out.println("Returned list of userlogins: " + userStats);
            assertTrue(userStats != null);
            assertTrue(userStats.length() > 10);
        }

    }
}