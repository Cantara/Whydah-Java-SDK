package net.whydah.sso.commands.extensions.statistics;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationTokenID;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.ddd.WhydahIdentity;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.types.UserTokenID;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class CommandListApplicationActivitiesTest {
    static SystemTestBaseConfig config;
    private final static Logger log = LoggerFactory.getLogger(CommandGetUsersStatsTest.class);


    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }

    @Test
    public void testCommandListApplicationActivitiesTest() throws Exception {

        if (config.isStatisticsExtensionSystemtestEnabled()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(new ApplicationTokenID(myApplicationTokenID).isValid());

            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            String userId = UserXpathHelper.getUserIdFromUserTokenXml(userToken);
            assertTrue(new UserTokenID(userTokenId).isValid());
            assertTrue(new WhydahIdentity(userId).isValid());

            String userStats = new CommandListApplicationActivities(config.statisticsServiceUri, myApplicationTokenID, userTokenId, userId).execute();
            log.debug("Returned list of usersessions: " + userStats);
            assertTrue(userStats != null);
            assertTrue(userStats.length() > 10);
        }

    }
}