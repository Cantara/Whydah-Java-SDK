package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.commands.userauth.CommandGetUsertokenByUserticket;
import net.whydah.sso.commands.userauth.CommandSendSmsPin;
import net.whydah.sso.user.mappers.UserIdentityMapper;
import net.whydah.sso.user.types.UserIdentity;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class CommandCreatePinVerifiedUserTest {

    static SystemTestBaseConfig config;
    private static final Logger log = LoggerFactory.getLogger(CommandGetUsertokenByUserticket.class);

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testCommandCreatePinVerifiedUser() throws Exception {
        if (config.isSystemTestEnabled()) {

            UserToken adminUser = config.logOnSystemTestApplicationAndSystemTestUser();
            UserIdentity uir = getTestNewUserIdentity();
            String userIdentityJson = UserIdentityMapper.toJsonWithoutUID(uir);
            String ticket = "67678687";
            String phoneNo = "98765433";
            String pin = "3434";
            new CommandSendSmsPin(config.tokenServiceUri, config.myApplicationToken.getApplicationTokenId(), ApplicationTokenMapper.toXML(config.myApplicationToken), phoneNo, pin).execute();
            String userAddRoleResult = new CommandCreatePinVerifiedUser(config.tokenServiceUri, config.myApplicationToken.getApplicationTokenId(), ApplicationTokenMapper.toXML(config.myApplicationToken), adminUser.getTokenid(), ticket, phoneNo, pin, userIdentityJson).execute();
            System.out.println("testAddUser:" + userAddRoleResult);
            String usersListJson = new CommandListUsers(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUser.getTokenid(), "*").execute();
            System.out.println("usersListJson=" + usersListJson);
            assertTrue(usersListJson.indexOf(uir.getUsername()) > 0);
            String ut = new CommandGetUsertokenByUserticket(config.tokenServiceUri, config.myApplicationToken.getApplicationTokenId(), ApplicationTokenMapper.toXML(config.myApplicationToken), ticket).execute();

            log.debug("Returned UserToken: {}", ut);
            assertTrue(ut != null && ut.length() > 20);
        }

    }

    private UserIdentity getTestNewUserIdentity() {
        Random rand = new Random();
        rand.setSeed(new java.util.Date().getTime());
        UserIdentity user = new UserIdentity("TestPinUser-" + UUID.randomUUID().toString().replace("-", "").replace("_", "").substring(1, 10), "Mt Test", "Testesen", "0", UUID.randomUUID().toString().replace("-", "").replace("_", "").substring(1, 10) + "@getwhydah.com", "47" + Integer.toString(rand.nextInt(100000000)));
        return user;

    }
}


