package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.application.BaseConfig;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.mappers.UserIdentityMapper;
import net.whydah.sso.user.types.UserIdentity;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class CommandAddUserTest {

//    public static String TEMPORARY_APPLICATION_ID = "11";//"11";
//    public static String TEMPORARY_APPLICATION_NAME = "MyApp";//"11";
//    public static String TEMPORARY_APPLICATION_SECRET = "6r46g3q986Ep6By7B9J46m96D";
//    public static String userName = "admin";
//    public static String password = "whydahadmin";
//    private static URI tokenServiceUri;
//    private static ApplicationCredential appCredential;
//    private static UserCredential userCredential;
//    private static boolean systemTest = false;
//    private static URI userAdminServiceUri;
//    private static String userAdminService = "http://localhost:9992/useradminservice";
//    private static String userTokenService = "http://localhost:9998/tokenservice";

	static BaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
    	config = new BaseConfig();
//        appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
//        tokenServiceUri = UriBuilder.fromUri(userTokenService).build();
//        userCredential = new UserCredential(userName, password);
//
//        userAdminServiceUri = UriBuilder.fromUri(userAdminService).build();
//
//        if (systemTest) {
//            tokenServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/tokenservice/").build();
//            userAdminServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/tokenservice/").build();
//        }
//        SSLTool.disableCertificateValidation();
    }



//    @Test
//    public void testAddUser() throws Exception {
//
//        if (!SystemTestUtil.noLocalWhydahRunning()) {
//
//            String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
//            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
//            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
//            String userticket = UUID.randomUUID().toString();
//            String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
//            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
//            assertTrue(userTokenId != null && userTokenId.length() > 5);
//
//            UserIdentity uir = getTestNewUserIdentity(UserXpathHelper.getUserIdFromUserTokenXml(userToken), myApplicationTokenID);
//            String userIdentityJson = UserIdentityMapper.toJson(uir);
//            // URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String roleJson
//            String userAddRoleResult = new CommandAddUser(userAdminServiceUri, myApplicationTokenID, userTokenId, userIdentityJson).execute();
//            System.out.println("testAddUser:" + userAddRoleResult);
//
//            String usersListJson = new CommandListUsers(userAdminServiceUri, myApplicationTokenID, userTokenId, "").execute();
//            System.out.println("usersListJson=" + usersListJson);
//            assertTrue(usersListJson.indexOf(uir.getUsername()) > 0);
//        }
//
//    }
    
    

    //TODO:totto, failed on CommandAddUSer -> 	//Error 500 Internal Server Error
    @Test
    public void testAddUser() throws Exception {

        if (config.enableTesting()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            assertTrue(userTokenId != null && userTokenId.length() > 5);

            UserIdentity uir = getTestNewUserIdentity(UserXpathHelper.getUserIdFromUserTokenXml(userToken), myApplicationTokenID);
            String userIdentityJson = UserIdentityMapper.toJsonWithoutUID(uir);
            // URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String roleJson
            String userAddRoleResult = new CommandAddUser(config.userAdminServiceUri, myApplicationTokenID, userTokenId, userIdentityJson).execute();
            System.out.println("testAddUser:" + userAddRoleResult);

            String usersListJson = new CommandListUsers(config.userAdminServiceUri, myApplicationTokenID, userTokenId, "").execute();
            System.out.println("usersListJson=" + usersListJson);
            assertTrue(usersListJson.indexOf(uir.getUsername()) > 0);
        }

    }

    private UserIdentity getTestNewUserIdentity(String userTokenId, String applicationId) {
        Random rand = new Random();
        rand.setSeed(new java.util.Date().getTime());
        UserIdentity user = new UserIdentity("us" + UUID.randomUUID().toString().replace("-", "").replace("_", "").substring(1, 10), "Mt Test", "Testesen", "0", UUID.randomUUID().toString().replace("-", "").replace("_", "").substring(1, 10) + "@getwhydah.com", "47" + Integer.toString(rand.nextInt(100000000)));
        return user;

    }
}
