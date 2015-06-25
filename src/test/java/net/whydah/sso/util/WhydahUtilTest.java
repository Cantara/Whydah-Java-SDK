package net.whydah.sso.util;

import net.whydah.sso.WhydahUtil;
import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserIdentityRepresentation;
import net.whydah.sso.user.UserRole;
import net.whydah.sso.user.UserXpathHelper;
import org.junit.Before;
import org.junit.Ignore;
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
 * Created by baardl on 18.06.15.
 */
@Ignore
public class WhydahUtilTest {
    private static final Logger log = getLogger(WhydahUtilTest.class);

    public static final String TEMPORARY_APPLICATION_ID = "201";//"11";
    public static final String TEMPORARY_APPLICATION_SECRET = "bbbbbbbbbbbbbbbbbbbbbbbbb";
    private final String userAdminServiceUri = "http://localhost:9992/useradminservice";
    private final String userTokenServiceUri = "http://localhost:9998/tokenservice";
    private String myApplicationTokenID = null;
    private String myAppTokenXml = null;
    private URI tokenServiceUri = null;
    private UserCredential userCredential = null;
    private String adminUserTokenId = null;

    @Before
    public void setUp() throws Exception {
        URI tokenServiceUri = UriBuilder.fromUri(userTokenServiceUri).build();
        ApplicationCredential appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID,TEMPORARY_APPLICATION_SECRET);
        myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        userCredential = new UserCredential("altranadmin", "altranadmin");
        log.debug("Logged in service {}", myApplicationTokenID);
        String adminUserTokenXml = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, UUID.randomUUID().toString()).execute();
        adminUserTokenId = UserXpathHelper.getUserTokenId(adminUserTokenXml);
        log.debug("Logged in admin {}", adminUserTokenId);

    }

    @Test
    public void testLogOnApplicationAndUser() throws Exception {
        assertNotNull(adminUserTokenId);

    }

    @Test
    public void testAddUser() throws Exception {
        //Use token for add user
        String username = "_temp_username_" + System.currentTimeMillis();
        UserIdentityRepresentation userIdentity = new UserIdentityRepresentation(username,"first","last","ref",username +"@example.com","+4712345678");
        String userTokenXml = WhydahUtil.addUser(userAdminServiceUri, myApplicationTokenID, adminUserTokenId, userIdentity);
        assertNotNull(userTokenXml);
        String createdUserName = UserXpathHelper.getUserIdFromUserTokenXml(userTokenXml);
        assertFalse(createdUserName.contains("7583278592730985723"));
        assertEquals(username,createdUserName);
    }


    @Test
    public void testAddRoleToUser() throws Exception {

        //Use token for add user
        String username = "_temp_username4Role_" + System.currentTimeMillis();
        UserIdentityRepresentation userIdentity = new UserIdentityRepresentation(username,"first","last","ref",username +"@example.com","+4712345678");
        String userTokenXml = WhydahUtil.addUser(userAdminServiceUri,myApplicationTokenID,adminUserTokenId,userIdentity);
        assertNotNull(userTokenXml);
        String createdUserId = UserXpathHelper.getUserIdFromUserTokenXml(userTokenXml);
        assertFalse(createdUserId.contains("7583278592730985723"));
        //User is created, now add role
        String orgName = "testOrg";
        String roleName = "testRoleName";
        String roleValue = "true";
        UserRole role = new UserRole(createdUserId,TEMPORARY_APPLICATION_ID,orgName, roleName, roleValue);
        List<UserRole> roles = new ArrayList<>();
        roles.add(role);
        List<UserRole> result = WhydahUtil.addRolesToUser(userAdminServiceUri, myApplicationTokenID,adminUserTokenId, roles);
        assertNotNull(result);
        assertEquals(1, result.size());
        String roleId = result.get(0).getId();
        assertTrue(roleId.length() > 0);

    }

    @Test
    public void testListRolesForUserAndApplication() throws Exception {
        String username = "_temp_username4Role_" + System.currentTimeMillis();
        UserIdentityRepresentation userIdentity = new UserIdentityRepresentation(username,"first","last","ref",username +"@example.com","+4712345678");
        String userTokenXml = WhydahUtil.addUser(userAdminServiceUri,myApplicationTokenID,adminUserTokenId,userIdentity);
    }
}