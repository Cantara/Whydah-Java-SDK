package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.application.SystemTestBaseConfig;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class CommandGetUserTest {

    private static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testCommandGetUser() throws Exception {


        if (config.isSystemTestEnabled()) {
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();

            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            assertTrue(userTokenId != null && userTokenId.length() > 5);


            String userAggregateJson = new CommandGetUser(config.userAdminServiceUri, myApplicationTokenID, userTokenId, "useradmin").execute();
            System.out.println("userAggregateJson=" + userAggregateJson);
            UserToken foundUserToken = UserTokenMapper.fromUserAggregateJson(userAggregateJson);
            System.out.println(foundUserToken.toString());


        }


    }

}