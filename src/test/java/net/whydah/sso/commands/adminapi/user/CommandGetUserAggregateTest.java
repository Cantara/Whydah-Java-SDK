package net.whydah.sso.commands.adminapi.user;


import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.appauth.CommandLogonApplicationWithStubbedFallback;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredentialWithStubbedFallback;
import net.whydah.sso.user.UserXpathHelper;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SSLTool;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class CommandGetUserAggregateTest {

    private static URI tokenServiceUri;
    private static ApplicationCredential appCredential;
    private static UserCredential userCredential;
    private static boolean systemtest = true;
    private static URI userAdminServiceUri;


    @BeforeClass
    public static void setup() throws Exception {
        appCredential = new ApplicationCredential("15", "HK8fGpWmK66ckWaEVn3tF9fRK");
        tokenServiceUri = UriBuilder.fromUri("https://no_host").build();
        userCredential = new UserCredential("useradmin", "useradmin42");

        userAdminServiceUri = UriBuilder.fromUri("https://no_host").build();

        if (systemtest) {
            tokenServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/tokenservice/").build();
            userAdminServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/useradminservice/").build();
        }
        SSLTool.disableCertificateValidation();
    }


    @Test
    public void testGetUserAggregate() throws Exception {

        String myAppTokenXml;
        if (systemtest) {
            myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        } else {
            myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(tokenServiceUri, appCredential).execute();
        }
        System.out.println("myAppTokenXml:" + myAppTokenXml);
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
        String userticket = UUID.randomUUID().toString();

        String userToken;
        if (systemtest) {
            userToken = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
        } else {
            userToken = new CommandLogonUserByUserCredentialWithStubbedFallback(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
        }
        String userTokenId = UserXpathHelper.getUserTokenId(userToken);
        assertTrue(userTokenId != null && userTokenId.length() > 5);

        String usersListJson;
        if (systemtest) {
            usersListJson = new CommandGetUserAggregate(userAdminServiceUri, myApplicationTokenID, userTokenId, "useradmin").execute();
            System.out.println("userJson=" + usersListJson);
        }

    }

}