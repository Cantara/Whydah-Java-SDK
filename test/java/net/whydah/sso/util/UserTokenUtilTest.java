package net.whydah.sso.util;

import net.whydah.sso.WhydahUtil;
import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import javax.ws.rs.core.UriBuilder;
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
    public static String TEMPORARY_APPLICATION_SECRET = "6r46g3q986Ep6By7B9J46m96D";
    public static String userName = "admin";
    public static String password = "whydahadmin";
    private final String userAdminServiceUri = "http://localhost:9992/useradminservice";
    private final String userTokenServiceUri = "http://localhost:9998/tokenservice";
    private String myApplicationTokenID = null;
    private String myAppTokenXml = null;
    private URI tokenServiceUri = null;
    private UserCredential userCredential = null;
    private String adminUserTokenId = null;
    private String adminUserId = null;

    private final String orgName = "testOrg";
    private final String roleName = "TestXXRoleName_"+UUID.randomUUID();
    private final String roleValue = "true";

    @Before
    public void setUp() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {
            tokenServiceUri = UriBuilder.fromUri(userTokenServiceUri).build();
            ApplicationCredential appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_SECRET);
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
    public void testFindValidUserTokenByUserTokenId() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {
            String roleValue = "true";
            UserRole role = new UserRole(userName, TEMPORARY_APPLICATION_ID, orgName, roleName, roleValue);
            List<UserRole> roles = new ArrayList<>();
            roles.add(role);
            List<UserRole> result = WhydahUtil.addRolesToUser(userAdminServiceUri, myApplicationTokenID, adminUserTokenId, roles);
            //Re-login after add roles
            String adminUserTokenXml = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, UUID.randomUUID().toString()).execute();
            adminUserTokenId = UserXpathHelper.getUserTokenId(adminUserTokenXml);

            String userTokenXml = UserTokenUtil.findValidUserTokenByUserTokenId(userTokenServiceUri, myApplicationTokenID, "anythihng", adminUserTokenId);
            assertNotNull(userTokenXml);

            String expression = "/usertoken/uid";
            String userId = UserXpathHelper.findValue(userTokenXml, expression);
            assertEquals("admin", userId);
            UserRole[] userRoles = UserRoleXpathHelper.getUserRoleFromUserTokenXml(userTokenXml);
            assertTrue(UserXpathHelper.hasRoleFromUserToken(userTokenXml, TEMPORARY_APPLICATION_ID, roleName));


        }
    }
}