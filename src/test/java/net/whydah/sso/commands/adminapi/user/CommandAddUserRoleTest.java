package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.adminapi.application.CommandListApplications;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SSLTool;
import net.whydah.sso.util.SystemTestUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CommandAddUserRoleTest {
    public static String TEMPORARY_APPLICATION_ID = "2211";//"11";
    public static String TEMPORARY_APPLICATION_NAME = "MyApp";//"11";
    public static String TEMPORARY_APPLICATION_SECRET = "NmHsQDCerVWx5d6aCjug9fyPE";
    public static String userName = "useradmin";
    public static String password = "useradmin42";
    private static ApplicationCredential appCredential;
    private static UserCredential userCredential;
    private static URI tokenServiceUri;
    private static URI userAdminServiceUri;
    private static boolean systemTest = false;


    @BeforeClass
    public static void setup() throws Exception {
        appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
        userCredential = new UserCredential(userName, password);

        if (systemTest) {
            tokenServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/tokenservice/").build();
            userAdminServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/tokenservice/").build();
            SSLTool.disableCertificateValidation();
        } else {
            String userAdminService = "http://localhost:9992/useradminservice";
            String userTokenService = "http://localhost:9998/tokenservice";
            tokenServiceUri = UriBuilder.fromUri(userTokenService).build();
            userAdminServiceUri = UriBuilder.fromUri(userAdminService).build();
        }
    }


    /**
     * Fails because no rebuild of active STS cache when new roles are added
     * @throws Exception
     */
    @Test
    public void testAddUserRole() throws Exception {

        if (!SystemTestUtil.noLocalWhydahRunning()||systemTest) {

            String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            String uId = UserXpathHelper.getUserIdFromUserTokenXml(userToken);
            assertTrue(userTokenId != null && userTokenId.length() > 5);


            String userRoleJson = getTestNewUserRole(UserXpathHelper.getUserIdFromUserTokenXml(userToken), TEMPORARY_APPLICATION_ID);
            // URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String roleJson
            String userAddRoleResult = new CommandAddUserRole(userAdminServiceUri, myApplicationTokenID, userTokenId, uId, userRoleJson).execute();
            System.out.println("userAddRoleResult:" + userAddRoleResult);
            assertNotNull(userAddRoleResult);

            // Force update with new role
            String userToken2 = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
            System.out.println("userToken2:" + userToken2);
            assertTrue(userToken2.length() > userToken.length());

            String applicationsJson = new CommandListApplications(userAdminServiceUri, myApplicationTokenID, userTokenId, "").execute();
            System.out.println("applicationsJson=" + applicationsJson);
            assertNotNull(applicationsJson);
//        assertNotNull(applicationsJson);
//        assertTrue(applicationsJson.equalsIgnoreCase(ApplicationHelper.getDummyAppllicationListJson()));
        }

    }

    private String getTestNewUserRole(String userTokenId, String applicationId) {
        UserApplicationRoleEntry role = new UserApplicationRoleEntry(userTokenId, applicationId, "TestOrg" + UUID.randomUUID(), "TestRolename" + UUID.randomUUID(), "testRoleValue");

        return role.toJson();

    }
}
