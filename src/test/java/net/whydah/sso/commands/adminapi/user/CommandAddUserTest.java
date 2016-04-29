package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.application.SystemTestBaseConfig;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.mappers.UserIdentityMapper;
import net.whydah.sso.user.types.UserIdentity;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class CommandAddUserTest {

    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }




    @Test
    public void testAddUser() throws Exception {

        if (config.isSystemTestEnabled()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            assertTrue(userTokenId != null && userTokenId.length() > 5);

            UserIdentity uir = getTestNewUserIdentity(UserXpathHelper.getUserIdFromUserTokenXml(userToken), myApplicationTokenID);
            String userIdentityJson = UserIdentityMapper.toJsonWithoutUID(uir);
            // URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String roleJson
            String userAddRoleResult = new CommandAddUser(config.userAdminServiceUri, myApplicationTokenID, userTokenId, userIdentityJson).execute();
            System.out.println("testAddUser:" + userAddRoleResult);

            String usersListJson = new CommandListUsers(config.userAdminServiceUri, myApplicationTokenID, userTokenId, "*").execute();
            System.out.println("usersListJson=" + usersListJson);
            assertTrue(usersListJson.indexOf(uir.getUsername()) > 0);
        }

    }

    private UserIdentity getTestNewUserIdentity(String userTokenId, String applicationId) {
        Random rand = new Random();
        rand.setSeed(new java.util.Date().getTime());
        UserIdentity user = new UserIdentity("us" + UUID.randomUUID().toString().replace("-", "").replace("_", "").substring(1, 10), "Mt Test", "Testesen", "0", UUID.randomUUID().toString().replace("-", "").replace("_", "").substring(1, 10) + "@getwhydah.com", "47" + Integer.toString(rand.nextInt(100000000)));
        return user;

    }
}
