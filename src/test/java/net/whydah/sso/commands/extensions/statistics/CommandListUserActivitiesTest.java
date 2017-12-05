package net.whydah.sso.commands.extensions.statistics;

import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.session.WhydahUserSession;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class CommandListUserActivitiesTest {
    private final static Logger log = LoggerFactory.getLogger(CommandListUserActivitiesTest.class);
    public static String userName = "admin";
    private static WhydahApplicationSession whydahApplicationSession;
    private static WhydahUserSession whydahUserSession;

    static SystemTestBaseConfig config;


    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
        if (config.isStatisticsExtensionSystemtestEnabled()) {
            whydahApplicationSession = WhydahApplicationSession.getInstance(config.tokenServiceUri.toString(), config.appCredential);
            whydahUserSession = new WhydahUserSession(whydahApplicationSession, new UserCredential(config.userName, config.password));
        }

    }

    @Test
    public void testCommandListUserActivitiesTest() throws Exception {

        if (config.isStatisticsExtensionSystemtestEnabled()) {


            String userStats = new CommandListUserActivities(config.statisticsServiceUri, whydahApplicationSession.getActiveApplicationTokenId(), whydahUserSession.getActiveUserTokenId(), userName).execute();
            assertTrue(userStats != null);
            log.debug("Returned list of usersessions: " + userStats);
            assertTrue(userStats.length() > 10);
        }

    }
}