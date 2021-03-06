package net.whydah.sso.commands.userauth;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

public class CommandCheckUserTokenIdTest {

    static SystemTestBaseConfig config;
    private static final Logger log = getLogger(CommandCheckUserTokenIdTest.class);


    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testCommandValidateUsertokenId() throws Exception {

        if (config.isSystemTestEnabled()) {
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            log.debug(myAppTokenXml);
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            log.debug(myApplicationTokenID);

            assertTrue(ApplicationTokenID.isValid(myApplicationTokenID));


            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential).execute();

            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            boolean isvalidToken = new CommandValidateUserTokenId(config.tokenServiceUri, myApplicationTokenID, userTokenId).execute();
            assertTrue("Error: usertokenid NOT verified successfully", isvalidToken);

        }

    }

    @Test
    public void testCommandValidateUsertokenId2() throws Exception {

        if (config.isSystemTestEnabled()) {
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            log.debug(myAppTokenXml);
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            log.debug(myApplicationTokenID);

            assertTrue(myApplicationTokenID.length() > 6);


            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential).execute();

            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            boolean isvalidToken = new CommandValidateUserTokenId(config.tokenServiceUri, myApplicationTokenID, userTokenId).execute();
            assertTrue("Error: usertokenid NOT verified successfully", isvalidToken);
            isvalidToken = new CommandValidateUserTokenId(config.tokenServiceUri, myApplicationTokenID, userTokenId).execute();
            assertTrue("Error: usertokenid NOT verified successfully", isvalidToken);
            isvalidToken = new CommandValidateUserTokenId(config.tokenServiceUri, myApplicationTokenID, userTokenId).execute();
            assertTrue("Error: usertokenid NOT verified successfully", isvalidToken);
            isvalidToken = new CommandValidateUserTokenId(config.tokenServiceUri, myApplicationTokenID, userTokenId).execute();
            assertTrue("Error: usertokenid NOT verified successfully", isvalidToken);
            isvalidToken = new CommandValidateUserTokenId(config.tokenServiceUri, myApplicationTokenID, userTokenId).execute();
            assertTrue("Error: usertokenid NOT verified successfully", isvalidToken);

        }

    }

}

