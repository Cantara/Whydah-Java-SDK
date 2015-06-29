package net.whydah.sso.commands.adminapi;

import net.whydah.sso.WhydahUtil;
import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationHelper;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.appauth.CommandLogonApplicationWithStubbedFallback;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredentialWithStubbedFallback;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserRole;
import net.whydah.sso.user.UserXpathHelper;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * Created by totto on 29.06.15.
 */
public class CommandAddUserRoleTest {
    private static URI tokenServiceUri;
    private static ApplicationCredential appCredential;
    private static UserCredential userCredential;
    private static boolean systemTest = false;
    private static URI userAdminServiceUri;
    public static String TEMPORARY_APPLICATION_ID = "99";//"11";
    public static String TEMPORARY_APPLICATION_SECRET = "33879936R6Jr47D4Hj5R6p9qT";
    public static String userName = "useradmin";
    public static String password = "useradmin567";

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
    public void testAddUserRole() throws Exception {

        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
        String userticket = UUID.randomUUID().toString();
        String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
        String userTokenId = UserXpathHelper.getUserTokenId(userToken);
        assertTrue(userTokenId != null && userTokenId.length() > 5);


        String userRoleJson = getTestNewUserRole(UserXpathHelper.getUserIdFromUserTokenXml(userToken), myApplicationTokenID);
        // URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String roleJson
        String userAddRoleResult = new CommandAddUserRole(userAdminServiceUri, myApplicationTokenID, userTokenId, userRoleJson).execute();
        System.out.println("userAddRoleResult:" + userAddRoleResult);

        // Force update with new role
        String userToken2 = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
        System.out.println("userToken2:" + userToken2);

        String applicationsJson = new CommandListApplications(userAdminServiceUri, myApplicationTokenID, userTokenId, "").execute();
        System.out.println("applicationsJson=" + applicationsJson);
        assertTrue(applicationsJson.equalsIgnoreCase(ApplicationHelper.getDummyAppllicationListJson()));

    }

    private String getTestNewUserRole(String userTokenId, String applicationId) {
        UserRole role = new UserRole(userTokenId, applicationId, "TestOrg", "TestRolename", "testRoleValue");

        return role.toJson();

    }
}
