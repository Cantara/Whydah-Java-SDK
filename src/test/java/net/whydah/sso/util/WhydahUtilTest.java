package net.whydah.sso.util;

import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.commands.CommandLogonApplication;
import net.whydah.sso.commands.CommandLogonUserByUserCredential;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserIdentityRepresentation;
import net.whydah.sso.user.UserXpathHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by baardl on 18.06.15.
 */
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
        myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppToken(myAppTokenXml);
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
        String userId = WhydahUtil.addUser(userAdminServiceUri,myApplicationTokenID,adminUserTokenId,userIdentity);
        assertNotNull(userId);
        log.debug("Added User {}", userId);
        assertFalse(userId.contains("7583278592730985723"));
    }

    @Ignore
    @Test
    public void testAddRoleToUser() throws Exception {

        log.debug("Logged in service {}", myApplicationTokenID);
        UserCredential userCredential = new UserCredential("altranadmin", "altranadmin");
        String adminUserTokenXml = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, UUID.randomUUID().toString()).execute();
        String adminUserTokenId = UserXpathHelper.getUserTokenId(adminUserTokenXml);
        log.debug("Logged in admin {}", adminUserTokenId);
        //WhydahUtil.logOnApplicationAndUser(userTokenServiceUri,TEMPORARY_APPLICATION_ID,TEMPORARY_APPLICATION_SECRET,"altranadmin","altranadmin");
        //Use token for add user
        String username = "_temp_username_" + System.currentTimeMillis();
        UserIdentityRepresentation userIdentity = new UserIdentityRepresentation(username,"first","last","ref",username +"@example.com","+4712345678");
        String userId = WhydahUtil.addUser(userAdminServiceUri,myApplicationTokenID,adminUserTokenId,userIdentity);
        assertNotNull(userId);
        log.debug("Added User {}", userId);
        assertFalse(userId.contains("7583278592730985723"));
    }
}