package net.whydah.sso.user.util;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserRoleXpathHelper;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SystemTestUtil;
import net.whydah.sso.util.WhydahUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by baardl on 23.06.15.
 */
public class UserTokenUtilTest {
    private static final Logger log = getLogger(UserTokenUtilTest.class);

    public static String TEMPORARY_APPLICATION_ID = "11";//"11";
    public static String TEMPORARY_APPLICATION_NAME = "MyApp";//"11";
    public static String TEMPORARY_APPLICATION_SECRET = "6r46g3q986Ep6By7B9J46m96D";
    public static String userName = "admin";
    public static String password = "whydahadmin";
    private final String userAdminServiceUri = "http://localhost:9992/useradminservice";
    private final String userTokenServiceUri = "http://localhost:9998/tokenservice";
    private final String orgName = "testOrg";
    private final String roleName = "TestXXRoleName_" + UUID.randomUUID();
    private final String roleName2 = "TestXXRoleName_" + UUID.randomUUID();
    private final String roleValue = "true";
    private String myApplicationTokenID = null;
    private String myAppTokenXml = null;
    private URI tokenServiceUri = null;
    private UserCredential userCredential = null;
    private String adminUserTokenId = null;
    private String adminUserId = null;

    @Before
    public void setUp() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {
            tokenServiceUri = URI.create(userTokenServiceUri);
            ApplicationCredential appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
            myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
            myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            userCredential = new UserCredential(userName, password);
            String adminUserTokenXml = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, UUID.randomUUID().toString()).execute();
            adminUserTokenId = UserXpathHelper.getUserTokenId(adminUserTokenXml);
            adminUserId = UserXpathHelper.getUserIdFromUserTokenXml(adminUserTokenXml);
        }
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAddUserRolesAndFindIt() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {
            String roleValue = "true";
            UserApplicationRoleEntry role = new UserApplicationRoleEntry(userName, TEMPORARY_APPLICATION_ID, orgName, roleName, roleValue);
            List<UserApplicationRoleEntry> roles = new ArrayList<>();
            roles.add(role);
            role = new UserApplicationRoleEntry(userName, TEMPORARY_APPLICATION_ID, orgName, roleName2, roleValue);
            roles.add(role);
            WhydahUtil.addRolesToUser(userAdminServiceUri, myApplicationTokenID, adminUserTokenId, roles);
            //Re-login after add roles
            String adminUserTokenXml = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, UUID.randomUUID().toString()).execute();
            adminUserTokenId = UserXpathHelper.getUserTokenId(adminUserTokenXml);

            String userTokenXml = WhydahUtil.getUserTokenByUserTokenId(userTokenServiceUri, myApplicationTokenID, "anythihng", adminUserTokenId);
            assertNotNull(userTokenXml);

            String expression = "/usertoken/uid";
            String userId = UserXpathHelper.getUserIdFromUserTokenXml(userTokenXml);
            assertEquals("admin", userId);
            UserApplicationRoleEntry[] userRoles = UserRoleXpathHelper.getUserRoleFromUserTokenXml(userTokenXml);
            assertTrue(UserXpathHelper.hasRoleFromUserToken(userTokenXml, TEMPORARY_APPLICATION_ID, roleName));
            assertTrue(UserXpathHelper.hasRoleFromUserToken(userTokenXml, TEMPORARY_APPLICATION_ID, roleName2));


        }
    }
}