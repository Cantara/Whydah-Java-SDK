package net.whydah.sso.commands.userauth;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import junit.framework.Assert;
import net.whydah.sso.application.ApplicationCredentialDummy;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplicationWithStubbedFallback;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserXpathHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static org.junit.Assert.assertTrue;

/**
 * Created by totto on 25.06.15.
 */
public class CommandGetUsertokenByUsertokenIdTest {
    private static URI tokenServiceUri;
    private static ApplicationCredentialDummy appCredential;
    private static UserCredential userCredential;
    private static boolean integrationMode = false;


    @BeforeClass
    public static void setup() throws Exception {
        tokenServiceUri = UriBuilder.fromUri("https://no_host").build();
        if (integrationMode) {
            tokenServiceUri = UriBuilder.fromUri("https://whydahdev.altrancloud.com/tokenservice/").build();
        }
        appCredential = new ApplicationCredentialDummy();
        appCredential.setApplicationID("15");
        String applicationsecret = "33779936R6Jr47D4Hj5R6p9qT";

        appCredential.setApplicationSecret(applicationsecret);
        userCredential = new UserCredential("useradmin", "useradmin42");

        // HystrixCommandProperties.Setter().withFallbackEnabled(!integrationMode);
        HystrixRequestContext context = HystrixRequestContext.initializeContext();

    }


    @Test
    public void testApplicationLoginCommand() throws Exception {

        String myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(tokenServiceUri, appCredential).execute();
        System.out.println(myAppTokenXml);
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        System.out.println(myApplicationTokenID);

        assertTrue(myApplicationTokenID.length() > 6);


        String userToken = new CommandLogonUserByUserCredentialWithStubbedFallback(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential).execute();

        String userTokenId = UserXpathHelper.getUserTokenId(userToken);
        String userToken2 = new CommandGetUsertokenByUsertokenIdWithStubbedFallback(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userTokenId).execute();

        Assert.assertTrue(userToken.equalsIgnoreCase(userToken2));


    }


}