package net.whydah.sso.commands.extensions.statistics;

import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.UUID;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.util.SystemTestBaseConfig;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandGetUsersStatsTest {

    static SystemTestBaseConfig config;
    private final static Logger log = LoggerFactory.getLogger(CommandGetUsersStatsTest.class);


    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }

    @Test
    public void testGetUsersStatsCommand() throws Exception {

        if (config.isStatisticsExtensionSystemtestEnabled()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID.length() > 10);

            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            assertTrue(userTokenId.length() > 10);

            Instant now = Instant.now();
            Instant startFrom = now.minusSeconds(60*60);
            String userStats = new CommandGetUserActivityStats(config.statisticsServiceUri, "whydah", "usersession", null, startFrom, now).execute();
            log.debug("Returned list of userlogins: " + userStats);     
            assertTrue(userStats != null);
            assertTrue(userStats.length() > 10);
        }

    }
}