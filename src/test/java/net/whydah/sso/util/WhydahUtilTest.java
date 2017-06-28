package net.whydah.sso.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserTokenXpathHelper;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.user.types.UserIdentity;
import net.whydah.sso.user.types.UserToken;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

/**
 * Created by baardl on 18.06.15.
 */
public class WhydahUtilTest {
    private static final Logger log = getLogger(WhydahUtilTest.class);
//    public static String TEMPORARY_APPLICATION_ID = "11";//"11";
//    public static String TEMPORARY_APPLICATION_NAME = "MyApp";//"11";
//    public static String TEMPORARY_APPLICATION_SECRET = "6r46g3q986Ep6By7B9J46m96D";
//    public static String userName = "admin";
//    public static String password = "whydahadmin";
//    //    public static final String TEMPORARY_APPLICATION_ID = "99";//"11";
////    public static final String TEMPORARY_APPLICATION_SECRET = "33879936R6Jr47D4Hj5R6p9qT";
//    private final String userAdminServiceUri = "http://localhost:9992/useradminservice";
//    private final String userTokenServiceUri = "http://localhost:9998/tokenservice";
//    private String myApplicationTokenID = null;
//    private String myAppTokenXml = null;
//    private URI tokenServiceUri = null;
//    private UserCredential userCredential = null;
//    private String adminUserTokenId = null;
    
    SystemTestBaseConfig config;

    @Before
    public void setUp() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {
//            URI tokenServiceUri = URI.create(userTokenServiceUri);
//            ApplicationCredential appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
//            myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
//            myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
//            userCredential = new UserCredential(userName, password);
//            log.debug("Logged in service {}", myApplicationTokenID);
//            String adminUserTokenXml = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, UUID.randomUUID().toString()).execute();
//            adminUserTokenId = UserXpathHelper.getUserTokenId(adminUserTokenXml);
//            log.debug("Logged in admin {}", adminUserTokenId);
        	config = new SystemTestBaseConfig();
        }

    }

//    @Test
//    public void testLogOnApplicationAndUser() throws Exception {
//        if (!SystemTestUtil.noLocalWhydahRunning()) {
//            assertNotNull(adminUserTokenId);
//        }
//
//    }

    @Test
    public void testAddUser() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {
        	UserToken ut = config.logOnSystemTestApplicationAndSystemTestUser();
            //Use token for add user
            String username = "_temp_username_" + System.currentTimeMillis();
            UserIdentity userIdentity = new UserIdentity(username, "first", "last", "ref", username + "@example.com", "+4712345678");
            String userTokenString = WhydahUtil.addUser(config.userAdminServiceUri.toString(), config.myApplicationTokenID, ut.getTokenid(), userIdentity);
            UserToken mUT = UserTokenMapper.fromUserIdentityJson(userTokenString);
            assertNotNull(mUT);
           
            assertFalse(userTokenString.contains("7583278592730985723"));
            assertEquals(username, mUT.getUserName());
        }
    }


    @Test
    public void testAddRoleToUser() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {
        	UserToken ut = config.logOnSystemTestApplicationAndSystemTestUser();
            //Use token for add user
            String username = "_temp_username4Role_" + System.currentTimeMillis();
            UserIdentity userIdentity = new UserIdentity(username, "first", "last", "ref", username + "@example.com", "+4712345678");
            String userTokenString = WhydahUtil.addUser(config.userAdminServiceUri.toString(), config.myApplicationTokenID, ut.getTokenid(), userIdentity);
            assertNotNull(userTokenString);
            UserToken mUT = UserTokenMapper.fromUserIdentityJson(userTokenString);
            
            String createdUserId = mUT.getUid();
            assertFalse(createdUserId.contains("7583278592730985723"));
            //User is created, now add role
            String orgName = "testOrg";
            String roleName = "testRoleName";
            String roleValue = "true";
            UserApplicationRoleEntry role = new UserApplicationRoleEntry(username, config.TEMPORARY_APPLICATION_ID, orgName, roleName, roleValue);
            role.setUserId(createdUserId);
            List<UserApplicationRoleEntry> roles = new ArrayList<>();
            roles.add(role);
            List<UserApplicationRoleEntry> result = WhydahUtil.addRolesToUser(config.userAdminServiceUri.toString(), config.myApplicationTokenID, ut.getTokenid(), roles);
            assertNotNull(result);
            assertEquals(1, result.size());
            String roleId = result.get(0).getId();
            assertTrue(roleId.length() > 0);
        }

    }

    @Test
    public void testListRolesForUserAndApplication() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {
        	UserToken ut = config.logOnSystemTestApplicationAndSystemTestUser();
            String username = "_temp_username4Role_" + System.currentTimeMillis();
            UserIdentity userIdentity = new UserIdentity(username, "first", "last", "ref", username + "@example.com", "+4712345678");
            String userTokenResult = WhydahUtil.addUser(config.userAdminServiceUri.toString(), config.myApplicationTokenID, ut.getTokenid(), userIdentity);
            assertNotNull(userTokenResult);
        }
    }
}