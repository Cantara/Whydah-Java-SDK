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

import java.time.Instant;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class CommandGetUsersStatsTest {

    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }

    @Test
    public void testGetUsersStatsCommand() throws Exception {

        if (config.isStatisticsExtensionSystemtestEnabled()) {

            String myApplicationTokenID = "";
            SSLTool.disableCertificateValidation();
            ApplicationCredential appCredential = new ApplicationCredential(config.TEMPORARY_APPLICATION_ID, config.TEMPORARY_APPLICATION_NAME, config.TEMPORARY_APPLICATION_SECRET);
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, appCredential).execute();
            myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID.length() > 10);

            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            assertTrue(userTokenId.length() > 10);

            Instant now = Instant.now();
            Instant lessOneHour = now.minusSeconds(60 * 60);
            String userStats = new CommandGetUsersStats(config.statisticsServiceUri, myApplicationTokenID, userTokenId, lessOneHour, now).execute();
            System.out.println("Returned list of userlogins: " + userStats);
            assertTrue(userStats != null);
            assertTrue(userStats.length() > 10);
        }

    }
}