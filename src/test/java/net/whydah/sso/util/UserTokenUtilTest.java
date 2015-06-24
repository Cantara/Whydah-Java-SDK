package net.whydah.sso.util;

import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.commands.CommandLogonApplication;
import net.whydah.sso.commands.CommandLogonUserByUserCredential;
import net.whydah.sso.user.*;
import org.junit.After;
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
 * Created by baardl on 23.06.15.
 */
public class UserTokenUtilTest {
    private static final Logger log = getLogger(UserTokenUtilTest.class);

    public static final String TEMPORARY_APPLICATION_ID = "201";//"11";
    public static final String TEMPORARY_APPLICATION_SECRET = "bbbbbbbbbbbbbbbbbbbbbbbbb";
    private final String userAdminServiceUri = "http://localhost:9992/useradminservice";
    private final String userTokenServiceUri = "http://localhost:9998/tokenservice";
    private String myApplicationTokenID = null;
    private String myAppTokenXml = null;
    private URI tokenServiceUri = null;
    private UserCredential userCredential = null;
    private String adminUserTokenId = null;
    private String adminUserId = null;

    private final String orgName = "testOrg";
    private final String roleName = "testRoleName";
    private final String roleValue = "true";

    @Before
    public void setUp() throws Exception {
        tokenServiceUri = UriBuilder.fromUri(userTokenServiceUri).build();
        ApplicationCredential appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID,TEMPORARY_APPLICATION_SECRET);
        myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        userCredential = new UserCredential("altranadmin", "altranadmin");
        String adminUserTokenXml = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, UUID.randomUUID().toString()).execute();
        adminUserTokenId = UserXpathHelper.getUserTokenId(adminUserTokenXml);
        adminUserId = UserXpathHelper.getUserId(adminUserTokenXml);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Ignore
    @Test
    public void testFindValidUserTokenByUserTokenId() throws Exception {
        String orgName = "testOrg";
        String roleName = "testRoleName";
        String roleValue = "true";
        UserRole role = new UserRole("altranadmin",TEMPORARY_APPLICATION_ID,orgName, roleName, roleValue);
        List<UserRole> roles = new ArrayList<>();
        roles.add(role);
        List<UserRole> result = WhydahUtil.addRolesToUser(userAdminServiceUri, myApplicationTokenID,adminUserTokenId, roles);
        //Re-login after add roles
        String adminUserTokenXml = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, UUID.randomUUID().toString()).execute();
        adminUserTokenId = UserXpathHelper.getUserTokenId(adminUserTokenXml);

        String userTokenXml = UserTokenUtil.findValidUserTokenByUserTokenId(userTokenServiceUri,myApplicationTokenID,"anythihng",adminUserTokenId);
        assertNotNull(userTokenXml);

        String expression = "/usertoken/uid";
        String userId = UserXpathHelper.findValue(userTokenXml, expression);
        assertEquals("altranadmin", userId);
        UserRole[] userRoles = UserRoleXPathHelper.getUserRoleFromUserTokenXml(userTokenXml);
        assertEquals("testRoleName", userRoles[1].getRoleName());

    }

    String addUserAndRole(){

        //Use token for add user
        String username = "_temp_username4Role_" + System.currentTimeMillis();
        UserIdentityRepresentation userIdentity = new UserIdentityRepresentation(username,"first","last","ref",username +"@example.com","+4712345678");
        String userTokenXml = WhydahUtil.addUser(userAdminServiceUri,myApplicationTokenID,adminUserTokenId,userIdentity);
        assertNotNull(userTokenXml);
        String createdUserId = UserXpathHelper.getUserId(userTokenXml);
        log.debug("Created userId {}", createdUserId);
        assertFalse(createdUserId.contains("7583278592730985723"));
        //User is created, now add role

        UserRole role = new UserRole(createdUserId,TEMPORARY_APPLICATION_ID,orgName, roleName, roleValue);
        List<UserRole> roles = new ArrayList<>();
        roles.add(role);
        List<UserRole> result = WhydahUtil.addRolesToUser(userAdminServiceUri, myApplicationTokenID,adminUserTokenId, roles);
        assertNotNull(result);
        assertEquals(1, result.size());
        String roleId = result.get(0).getId();
        assertTrue(roleId.length() > 0);

        return createdUserId;

    }
}