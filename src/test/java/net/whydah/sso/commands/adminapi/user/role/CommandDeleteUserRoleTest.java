package net.whydah.sso.commands.adminapi.user.role;

import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.commands.extensions.crmapi.CommandGetCRMCustomerTest;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.mappers.UserRoleMapper;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CommandDeleteUserRoleTest {
    private static final Logger log = LoggerFactory.getLogger(CommandGetCRMCustomerTest.class);
    public static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testAddAndDeleteUserRole() throws Exception {

        if (config.isSystemTestEnabled()) {

            UserToken adminUser = config.logOnSystemTestApplicationAndSystemTestUser();
            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, config.myApplicationToken.getApplicationTokenId(), ApplicationTokenMapper.toXML(config.myApplicationToken), config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            String uId = UserXpathHelper.getUserIdFromUserTokenXml(userToken);
            assertTrue(userTokenId != null && userTokenId.length() > 5);


            String userRoleJson = getTestNewUserRole(UserXpathHelper.getUserIdFromUserTokenXml(userToken), config.TEMPORARY_APPLICATION_ID);
            UserApplicationRoleEntry addedRole = UserRoleMapper.fromJson(userRoleJson);
            // URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String roleJson
            String userAddRoleResult = new CommandAddUserRole(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), userTokenId, uId, userRoleJson).execute();
            log.debug("userAddRoleResult:{}", userAddRoleResult);
            assertNotNull(userAddRoleResult);
 

            //count role after adding
            String userRolesJson = new CommandGetUserRoles(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUser.getTokenid(), adminUser.getUid()).execute();
            log.debug("Roles returned:" + userRolesJson);
            List<UserApplicationRoleEntry> roles = UserRoleMapper.fromJsonAsList(userRolesJson);
            int numberOfRolesAfterAdding = roles.size();
            
            UserApplicationRoleEntry newRoleAdded = UserRoleMapper.fromJson(userAddRoleResult);  
            boolean userDeleteRoleResult = new CommandDeleteUserRole(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), userTokenId, uId, newRoleAdded.getId()).execute();
            assertTrue(userDeleteRoleResult);
            
            //number of roles after removing 
            String userRolesJson2 = new CommandGetUserRoles(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUser.getTokenid(), adminUser.getUid()).execute();
            log.debug("Roles returned2:" + userRolesJson2);
            List<UserApplicationRoleEntry> roles2 = UserRoleMapper.fromJsonAsList(userRolesJson2);
            int numberOfRolesAfterRemoving = roles2.size();
            
            assertTrue(numberOfRolesAfterAdding == numberOfRolesAfterRemoving + 1);  // One role less

        }

    }

    private String getTestNewUserRole(String userTokenId, String applicationId) {
        UserApplicationRoleEntry role = new UserApplicationRoleEntry(userTokenId, applicationId, "TestOrg" + UUID.randomUUID(), "TestRolename" + UUID.randomUUID(), "testRoleValue");

        return role.toJson();

    }
}
