package net.whydah.sso.commands.extensions.statistics;

import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.session.WhydahUserSession;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CommandListApplicationActivitiesTest {
    private final static Logger log = LoggerFactory.getLogger(CommandGetUsersStatsTest.class);


    public static String userName = "admin";
    public static String password = "whydahadmin";
    private static WhydahApplicationSession applicationSession;

    static SystemTestBaseConfig config;




    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
        userName = config.userName;
        password = config.password;
        if (config.isStatisticsExtensionSystemtestEnabled()) {
            applicationSession = WhydahApplicationSession.getInstance(config.tokenServiceUri.toString(), config.appCredential);
        }

    }



    @Test
    public void testCommandListApplicationActivitiesTest() throws Exception {

        if (config.isStatisticsExtensionSystemtestEnabled()) {

            UserCredential userCredential = new UserCredential(userName, password);
            assertTrue(applicationSession.checkActiveSession());
            WhydahUserSession userSession = new WhydahUserSession(applicationSession, userCredential);
            assertTrue(userSession.hasActiveSession());
            assertNotNull(userSession.getActiveUserToken());
            assertTrue(userSession.getActiveUserToken().contains(config.userEmail));
            assertTrue(applicationSession.checkActiveSession());


            String userStats = new CommandListApplicationActivities(config.statisticsServiceUri, applicationSession.getActiveApplicationTokenId(), userSession.getActiveUserTokenId(), "100").execute();
            assertTrue(userStats != null);
            log.debug("Returned list of usersessions: " + userStats);
            assertTrue(userStats.length() > 10);
        }

    }
}