//package net.whydah.sso.commands.extensions.crmapi;
//
//import static org.junit.Assert.assertTrue;
//
//import java.util.UUID;
//
//import net.whydah.sso.application.helpers.ApplicationXpathHelper;
//import net.whydah.sso.commands.appauth.CommandLogonApplication;
//import net.whydah.sso.commands.extensions.statistics.CommandGetUsersStatsTest;
//import net.whydah.sso.commands.extensions.statistics.CommandListUserCRMActivities_old;
//import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
//import net.whydah.sso.user.helpers.UserXpathHelper;
//import net.whydah.sso.util.SystemTestBaseConfig;
//
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
//public class CommandListUserCRMActivitiesTest {
//    static SystemTestBaseConfig config;
//    private final static Logger log = LoggerFactory.getLogger(CommandGetUsersStatsTest.class);
//
//
//    @BeforeClass
//    public static void setup() throws Exception {
//        config = new SystemTestBaseConfig();
//    }
//
//    @Test
//    public void testCommandListUserActivitiesTest() throws Exception {
//
//        if (config.isStatisticsExtensionSystemtestEnabled()) {
//
//            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
//            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
//            assertTrue(myApplicationTokenID.length() > 10);
//
//            String userticket = UUID.randomUUID().toString();
//            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
//            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
//            String userId = UserXpathHelper.getUserIdFromUserTokenXml(userToken);
//            assertTrue(userTokenId.length() > 10);
//
//            String userStats = new CommandListUserCRMActivities_old(config.statisticsServiceUri, myApplicationTokenID, userTokenId, userId).execute();
//            log.debug("Returned list of crmactivities: " + userStats);
//            assertTrue(userStats != null);
//            assertTrue(userStats.length() > 10);
//        }
//
//    }
//}
