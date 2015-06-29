package net.whydah.sso.commands.adminapi;

import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationHelper;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * Created by totto on 29.06.15.
 */
public class CommandAddUserTest {

    private static URI tokenServiceUri;
    private static ApplicationCredential appCredential;
    private static UserCredential userCredential;
    private static boolean systemTest = false;
    private static URI userAdminServiceUri;
    public static String TEMPORARY_APPLICATION_ID = "99";//"11";
    public static String TEMPORARY_APPLICATION_SECRET = "33879936R6Jr47D4Hj5R6p9qT";
    public static String userName = "useradmin";
    public static String password = "useradmin42";

    private static String userAdminService = "http://localhost:9992/useradminservice";
    private static String userTokenService = "http://localhost:9998/tokenservice";


    @BeforeClass
    public static void setup() throws Exception {
        appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_SECRET);
        tokenServiceUri = UriBuilder.fromUri(userTokenService).build();
        userCredential = new UserCredential(userName, password);

        userAdminServiceUri = UriBuilder.fromUri(userAdminService).build();

        if (systemTest) {
            tokenServiceUri = UriBuilder.fromUri("https://whydahdev.altrancloud.com/tokenservice/").build();
            userAdminServiceUri = UriBuilder.fromUri("https://whydahdev.altrancloud.com/tokenservice/").build();
        }
    }



    @Ignore
    @Test
    public void testAddUser() throws Exception {

        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
        String userticket = UUID.randomUUID().toString();
        String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
        String userTokenId = UserXpathHelper.getUserTokenId(userToken);
        assertTrue(userTokenId != null && userTokenId.length() > 5);


        String userIdentityXml = getTestNewUserIdentity(UserXpathHelper.getUserIdFromUserTokenXml(userToken), myApplicationTokenID);
        // URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String roleJson
        String userAddRoleResult = new CommandAddUser(userAdminServiceUri, myApplicationTokenID, userTokenId, userIdentityXml).execute();
        System.out.println("testAddUser:" + userAddRoleResult);

        String usersListJson = new CommandListUsers(userAdminServiceUri, myApplicationTokenID,userTokenId,"").execute();
        System.out.println("usersListJson=" + usersListJson);

    }

    private String getTestNewUserIdentity(String userTokenId, String applicationId) {
        UserHelper.userDummyIdentityJson();
        // String username, String firstName, String lastName, String personRef, String email, String cellPhone
        UserIdentityRepresentation user = new UserIdentityRepresentation("testuser","Mt Test","Testesen","0","test@getwhydah.com","+47 12345678");

        return user.toJson();

    }
}
