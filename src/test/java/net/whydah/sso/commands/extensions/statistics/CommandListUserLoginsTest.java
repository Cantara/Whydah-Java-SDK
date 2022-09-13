package net.whydah.sso.commands.extensions.statistics;

import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.UUID;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.util.SystemTestBaseConfig;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandListUserLoginsTest {
    static SystemTestBaseConfig config;
    private final static Logger log = LoggerFactory.getLogger(SystemTestBaseConfig.class);


    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testUserLoginsCustomerCommand() throws Exception {
        if (config.isStatisticsExtensionSystemtestEnabled()) {
            String myApplicationTokenID = "";
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID.length() > 10);

            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            assertTrue(userTokenId.length() > 10);

            String userLogins = new CommandGetUserLogonStats(config.statisticsServiceUri, config.userName, Instant.now(), Instant.now().plusSeconds(30)).execute();
            log.debug("Returned list of userlogins: " + userLogins);
            assertTrue(userLogins != null);
            assertTrue(userLogins.length() > 10);

        }


    }
}