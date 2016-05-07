package net.whydah.sso.user.util;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.user.types.UserIdentity;
import net.whydah.sso.util.WhydahUtil;
import org.junit.Before;
import org.slf4j.Logger;

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
    public static final String TEMPORARY_APPLICATION_ID = "201";//"11";
    public static final String TEMPORARY_APPLICATION_SECRET = "bbbbbbbbbbbbbbbbbbbbbbbbb";
    private static final Logger log = getLogger(WhydaUtilUserRolesJacksonHelperTest.class);
    public static String TEMPORARY_APPLICATION_NAME = "MyApp";//"11";
    private final String userAdminServiceUri = "http://localhost:9992/useradminservice";
    private final String userTokenServiceUri = "http://localhost:9998/tokenservice";
    private final String orgName = "testOrg";
    private final String roleName = "testRoleName";
    private final String roleValue = "true";
    private String myApplicationTokenID = null;
    private String myAppTokenXml = null;
    private URI tokenServiceUri = null;
    private UserCredential userCredential = null;
    private String adminUserTokenId = null;
    private String addedUser = null;

    @Before
    public void setUp() throws Exception {
        URI tokenServiceUri = URI.create(userTokenServiceUri);
        ApplicationCredential appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
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

        //Use token for add user
        String username = "_temp_username4Role_" + System.currentTimeMillis();
        UserIdentity userIdentity = new UserIdentity(username, "first", "last", "ref", username + "@example.com", "+4712345678");
        String userTokenXml = WhydahUtil.addUser(userAdminServiceUri, myApplicationTokenID, adminUserTokenId, userIdentity);
        assertNotNull(userTokenXml);
        String createdUserId = UserXpathHelper.getUserIdFromUserTokenXml(userTokenXml);
        log.debug("Created userId {}", createdUserId);
        assertFalse(createdUserId.contains("7583278592730985723"));
        //User is created, now add role

        UserApplicationRoleEntry role = new UserApplicationRoleEntry(createdUserId, TEMPORARY_APPLICATION_ID, orgName, roleName, roleValue);
        List<UserApplicationRoleEntry> roles = new ArrayList<>();
        roles.add(role);
        List<UserApplicationRoleEntry> result = WhydahUtil.addRolesToUser(userAdminServiceUri, myApplicationTokenID, adminUserTokenId, roles);
        assertNotNull(result);
        assertEquals(1, result.size());
        String roleId = result.get(0).getId();
        assertTrue(roleId.length() > 0);

        return createdUserId;

    }



}
