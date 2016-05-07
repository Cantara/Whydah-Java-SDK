package net.whydah.sso.commands.userauth;

import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.UUID;

import net.whydah.sso.application.SystemTestBaseConfig;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.commands.adminapi.user.CommandCreatePinVerifiedUser;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.mappers.UserIdentityMapper;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserIdentity;
import net.whydah.sso.user.types.UserToken;

import org.junit.BeforeClass;
import org.junit.Test;

public class CommandCreateTicketForUserTokenIDTest {
	static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }

    
    private UserIdentity getTestNewUserIdentity() {
        Random rand = new Random();
        rand.setSeed(new java.util.Date().getTime());
        UserIdentity user = new UserIdentity("TestUser-" + UUID.randomUUID().toString().replace("-", "").replace("_", "").substring(1, 10), "Mt Test", "Testesen", "0", UUID.randomUUID().toString().replace("-", "").replace("_", "").substring(1, 10) + "@getwhydah.com", "47" + Integer.toString(rand.nextInt(100000000)));
        return user;

    }
    

    @Test
    public void testCommandCreateTicketForUserTokenID() throws Exception {

        if (config.isSystemTestEnabled()) {
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            System.out.println(myAppTokenXml);
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            System.out.println(myApplicationTokenID);

            assertTrue(myApplicationTokenID.length() > 6);

            UserToken admin = config.logOnSystemTestApplicationAndSystemTestUser();
            String ticket = UUID.randomUUID().toString();
            
        	//create a ticket
            assertTrue(new CommandCreateTicketForUserTokenID(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, ticket, admin.getTokenid()).execute());           
            
        }

    }
}
