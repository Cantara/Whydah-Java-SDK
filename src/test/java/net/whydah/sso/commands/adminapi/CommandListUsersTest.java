package net.whydah.sso.commands.adminapi;

import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplicationWithStubbedFallback;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredentialWithStubbedFallback;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserHelper;
import net.whydah.sso.user.UserXpathHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.UUID;

/**
 * Created by totto on 25.06.15.
 */
public class CommandListUsersTest  {

    private static URI tokenServiceUri;
    private static ApplicationCredential appCredential;
    private static UserCredential userCredential;
    private static boolean integrationMode = false;
    private static URI userAdminServiceUri;



    @BeforeClass
    public static void setup() throws Exception {
        appCredential = new ApplicationCredential("15","33779936R6Jr47D4Hj5R6p9qT");
        tokenServiceUri = UriBuilder.fromUri("https://no_host").build();
        userCredential = new UserCredential("useradmin", "useradmin42");

        userAdminServiceUri = UriBuilder.fromUri("https://no_host").build();

        if (integrationMode) {
            tokenServiceUri = UriBuilder.fromUri("https://whydahdev.altrancloud.com/tokenservice/").build();
            userAdminServiceUri = UriBuilder.fromUri("https://whydahdev.altrancloud.com/tokenservice/").build();
        }
    }


    @Test
    public void testApplicationLoginCommandFallback() throws Exception {

        String myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(tokenServiceUri, appCredential).execute();
        System.out.println(myAppTokenXml);
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        System.out.println(myApplicationTokenID);
        String userticket = UUID.randomUUID().toString();
        String userToken = new CommandLogonUserByUserCredentialWithStubbedFallback(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
        String userTokenId = UserXpathHelper.getUserTokenId(userToken);

        String usersListJson = new CommandListUsersWithStubbedFallback(userAdminServiceUri, myApplicationTokenID,userTokenId,"").execute();
        System.out.println("usersListJson=" + usersListJson);
        assertTrue(usersListJson.equalsIgnoreCase(UserHelper.getDummyUserListJson()));

    }

}
