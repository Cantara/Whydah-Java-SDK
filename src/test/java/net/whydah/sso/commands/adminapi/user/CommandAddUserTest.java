package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.application.SystemTestBaseConfig;
import net.whydah.sso.user.mappers.UserAggregateMapper;
import net.whydah.sso.user.types.UserAggregate;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
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

            UserToken adminUser = config.logOnSystemTestApplicationAndSystemTestUser();

            UserApplicationRoleEntry role = getTestNewUserRole(adminUser.getUid(), config.TEMPORARY_APPLICATION_ID);
            String userRoleJson = role.toJson();
            // URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String roleJson
            String userAddRoleResult = new CommandAddUserRole(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUser.getTokenid(), adminUser.getUid(), userRoleJson).execute();
            System.out.println("userAddRoleResult:" + userAddRoleResult);
            assertNotNull(userAddRoleResult);

            //     public CommandGetUserAggregate(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userID) {
            String userAggregateJson = new CommandGetUserAggregate(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUser.getTokenid(), adminUser.getUid()).execute();
            UserAggregate userAggregate = UserAggregateMapper.fromUserAggregateNoIdentityJson(userAggregateJson);
            // Force update with new role
            System.out.println("userAggregate:" + UserAggregateMapper.toJson(userAggregate));
            assertTrue(userAggregateJson.contains(role.getRoleName()));
        }

    }

    private UserApplicationRoleEntry getTestNewUserRole(String userTokenId, String applicationId) {
        UserApplicationRoleEntry role = new UserApplicationRoleEntry(userTokenId, applicationId, "TestOrg" + UUID.randomUUID(), "TestRolename" + UUID.randomUUID(), "testRoleValue");

        return role;

    }
}


