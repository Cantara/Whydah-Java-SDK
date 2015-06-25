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
 * Created by baardl on 22.06.15.
 */
public class WhydaUtilUserRolesJacksonHelperTest {
    private static final Logger log = getLogger(WhydaUtilUserRolesJacksonHelperTest.class);

    public static final String TEMPORARY_APPLICATION_ID = "201";//"11";
    public static final String TEMPORARY_APPLICATION_SECRET = "bbbbbbbbbbbbbbbbbbbbbbbbb";
    private final String userAdminServiceUri = "http://localhost:9992/useradminservice";
    private final String userTokenServiceUri = "http://localhost:9998/tokenservice";
    private String myApplicationTokenID = null;
    private String myAppTokenXml = null;
    private URI tokenServiceUri = null;
    private UserCredential userCredential = null;
    private String adminUserTokenId = null;
    private final String orgName = "testOrg";
    private final String roleName = "testRoleName";
    private final String roleValue = "true";
    private String addedUser = null;

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
        addedUser = addUserAndRole();

    }

    String addUserAndRole(){

        //Use token for add userauth
        String username = "_temp_username4Role_" + System.currentTimeMillis();
        UserIdentityRepresentation userIdentity = new UserIdentityRepresentation(username,"first","last","ref",username +"@example.com","+4712345678");
        String userTokenXml = WhydahUtil.addUser(userAdminServiceUri, myApplicationTokenID, adminUserTokenId, userIdentity);
        assertNotNull(userTokenXml);
        String createdUserId = UserXpathHelper.getUserIdFromUserTokenXml(userTokenXml);
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


     // TODO Baard - should this work on jenkins? -BLI: YES. Though an dependency on environment, or mock.
    @Ignore
    @Test
    public void listRolesForUserAndApplication() throws Exception {
        log.trace("List roles for userauth {} in appauth {}", addedUser,TEMPORARY_APPLICATION_ID);
        List<UserRole> roles = WhydahUtil.listUserRoles(userAdminServiceUri,myApplicationTokenID, adminUserTokenId,TEMPORARY_APPLICATION_ID,addedUser);
        assertNotNull(roles);
        assertTrue("Size of roles should be > 0",roles.size() > 0);
        for (UserRole role : roles) {
            log.debug("Role found {}", role);
            assertEquals(TEMPORARY_APPLICATION_ID, role.getApplicationId());
        }
    }


}
