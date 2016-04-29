package net.whydah.sso.commands.adminapi.user;


import net.whydah.sso.application.SystemTestBaseConfig;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.appauth.CommandLogonApplicationWithStubbedFallback;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredentialWithStubbedFallback;
import net.whydah.sso.user.helpers.UserXpathHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class CommandGetUserAggregateTest {

static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testGetUserAggregate() throws Exception {

        boolean systemtest = config.isSystemTestEnabled();
      String myAppTokenXml;
      if (systemtest) {
          myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
      } else {
          myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(config.tokenServiceUri, config.appCredential).execute();
      }
    	
      String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
	    assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
	    String userticket = UUID.randomUUID().toString();
        String userToken;
        if (systemtest) {
        	userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
        } else {
        	userToken = new CommandLogonUserByUserCredentialWithStubbedFallback(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
        }
        String userTokenId = UserXpathHelper.getUserTokenId(userToken);
        assertTrue(userTokenId != null && userTokenId.length() > 5);

        String usersListJson;
        if (systemtest) {
            usersListJson = new CommandGetUserAggregate(config.userAdminServiceUri, myApplicationTokenID, userTokenId, "useradmin").execute();
            System.out.println("userJson=" + usersListJson);
        }

    }
}