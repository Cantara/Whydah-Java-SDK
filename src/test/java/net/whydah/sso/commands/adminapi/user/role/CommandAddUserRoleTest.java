package net.whydah.sso.commands.adminapi.user.role;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.extensions.crmapi.CommandGetCRMCustomerTest;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.commands.userauth.CommandRefreshUserToken;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.mappers.UserRoleMapper;
import net.whydah.sso.user.mappers.UserTokenMapper;
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

public class CommandAddUserRoleTest {

    private static final Logger log = LoggerFactory.getLogger(CommandGetCRMCustomerTest.class);
    public static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testAddUserRole() throws Exception {

        if (config.isSystemTestEnabled()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            String uId = UserXpathHelper.getUserIdFromUserTokenXml(userToken);
            assertTrue(userTokenId != null && userTokenId.length() > 5);
            

            String userRoleJson = getTestNewUserRole(UserXpathHelper.getUserIdFromUserTokenXml(userToken), config.TEMPORARY_APPLICATION_ID);
            
            // URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String roleJson
            String userAddRoleResult = new CommandAddUserRole(config.userAdminServiceUri, myApplicationTokenID, userTokenId, uId, userRoleJson).execute();
            log.debug("userAddRoleResult:{}", userAddRoleResult);
            assertNotNull(userAddRoleResult);
            UserApplicationRoleEntry addedRole = UserRoleMapper.fromJson(userAddRoleResult);
            
            //should do a refresh here
//            String updatedUserTokenXML = (new CommandRefreshUserToken(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, userTokenId).execute());
//			log.debug("Updated UserToken: {}", updatedUserTokenXML);
//			if (updatedUserTokenXML != null && updatedUserTokenXML.length() > 10) {
//				
//			}
            Thread.sleep(5000);//wait to update
            // Force update with new role
            String userToken2 = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            //log.debug("userToken2:" + userToken2);
            //String userTokenId2 = UserXpathHelper.getUserTokenId(userToken2);
            //assertTrue(userToken2.length() >= userToken.length());
            UserToken oldUserToken = UserTokenMapper.fromUserTokenXml(userToken);
            UserToken newuserToken = UserTokenMapper.fromUserTokenXml(userToken2);
            List<UserApplicationRoleEntry> newroleList = newuserToken.getRoleList();
            assertTrue(newroleList.size() == oldUserToken.getRoleList().size() + 1);
            
            boolean found = false;
            for (UserApplicationRoleEntry role : newroleList) {
                log.debug("Found role: {}", UserRoleMapper.toJson(role));
                if (role.getRoleName().equalsIgnoreCase(addedRole.getRoleName())) {
                    found = true;
                }

            }
            assertTrue(found);
            
            
        }

    }

    private String getTestNewUserRole(String userTokenId, String applicationId) {
        UserApplicationRoleEntry role = new UserApplicationRoleEntry(userTokenId, applicationId, "TestOrg" + UUID.randomUUID(), "TestRolename" + UUID.randomUUID(), "testRoleValue");

        return role.toJson();

    }
}
