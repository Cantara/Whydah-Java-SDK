package net.whydah.sso.commands.extensions.statistics;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.util.SSLTool;
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

            SSLTool.disableCertificateValidation();
            ApplicationCredential appCredential = new ApplicationCredential(config.TEMPORARY_APPLICATION_ID, config.TEMPORARY_APPLICATION_NAME, config.TEMPORARY_APPLICATION_SECRET);
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID.length() > 10);

            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            String userId = UserXpathHelper.getUserIdFromUserTokenXml(userToken);
            assertTrue(userTokenId.length() > 10);

            String userStats = new CommandListApplicationActivities(config.statisticsServiceUri, myApplicationTokenID, "2215", userId).execute();
            log.debug("Returned list of usersessions: " + userStats);
            assertTrue(userStats != null);
            assertTrue(userStats.length() > 10);
        }

    }
}