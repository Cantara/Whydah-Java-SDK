package net.whydah.sso.commands.adminapi;

import net.whydah.sso.application.ApplicationCredentialDummy;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplicationWithStubbedFallback;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredentialWithStubbedFallback;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserHelper;
import net.whydah.sso.user.UserXpathHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * Created by totto on 25.06.15.
 */
public class CommandListUsersTest  {

    private static URI tokenServiceUri;
    private static ApplicationCredentialDummy appCredential;
    private static UserCredential userCredential;
    private static boolean systemtest = false;
    private static URI userAdminServiceUri;



    @BeforeClass
    public static void setup() throws Exception {
        appCredential = new ApplicationCredentialDummy("15","33779936R6Jr47D4Hj5R6p9qT");
        tokenServiceUri = UriBuilder.fromUri("https://no_host").build();
        userCredential = new UserCredential("useradmin", "useradmin42");

        userAdminServiceUri = UriBuilder.fromUri("https://no_host").build();

        if (systemtest) {
            tokenServiceUri = UriBuilder.fromUri("https://whydahdev.altrancloud.com/tokenservice/").build();
            userAdminServiceUri = UriBuilder.fromUri("https://whydahdev.altrancloud.com/tokenservice/").build();
        }
    }


    @Test
    public void testListUserssCommandWithFallback() throws Exception {

        String myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(tokenServiceUri, appCredential).execute();
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
        String userticket = UUID.randomUUID().toString();
        String userToken = new CommandLogonUserByUserCredentialWithStubbedFallback(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
        String userTokenId = UserXpathHelper.getUserTokenId(userToken);
        assertTrue(userTokenId!=null && userTokenId.length()>5);

        String usersListJson = new CommandListUsersWithStubbedFallback(userAdminServiceUri, myApplicationTokenID,userTokenId,"").execute();
        System.out.println("usersListJson=" + usersListJson);
        assertTrue(usersListJson.equalsIgnoreCase(UserHelper.getDummyUserListJson()));

    }

}
