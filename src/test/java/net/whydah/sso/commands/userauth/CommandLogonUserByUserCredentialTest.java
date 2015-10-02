package net.whydah.sso.commands.userauth;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.appauth.CommandLogonApplicationWithStubbedFallback;
import net.whydah.sso.user.UserXpathHelper;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SystemTestUtil;
import net.whydah.sso.util.WhydahUtil;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by totto on 12/2/14.
 */
public class CommandLogonUserByUserCredentialTest {

    private static URI tokenServiceUri;
    private static ApplicationCredential appCredential;
    private static UserCredential userCredential;
    private static boolean integrationMode = false;


    @BeforeClass
    public static void setup() throws Exception {
        tokenServiceUri = UriBuilder.fromUri("http://localhost:9998/tokenservice").build();
        if (integrationMode) {
            tokenServiceUri = UriBuilder.fromUri("https://whydahdev.altrancloud.com/tokenservice/").build();
        }
        appCredential = new ApplicationCredential("11", "6r46g3q986Ep6By7B9J46m96D");
        userCredential = new UserCredential("admin", "whydahadmin");

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

        String userticket = UUID.randomUUID().toString();

        myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(tokenServiceUri, appCredential).execute();
        myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        String userToken = new CommandLogonUserByUserCredentialWithStubbedFallback(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
        String userTokenId = UserXpathHelper.getUserTokenId(userToken);
        if (integrationMode) {
            assertTrue(new CommandValidateUsertokenId(tokenServiceUri, myApplicationTokenID, userTokenId).execute());
        }

        myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(tokenServiceUri, appCredential).execute();
        myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        String userToken2 = new CommandGetUsertokenByUserticket(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userticket).execute();


    }

    @Test
    public void tesLogOnApplicationAndUser() throws Exception {

        if (integrationMode) {
            String userToken = WhydahUtil.logOnApplicationAndUser(tokenServiceUri.toString(), appCredential.getApplicationID(), appCredential.getApplicationSecret(), userCredential.getUserName(), userCredential.getPassword());
            assertNotNull(userToken);
            assertTrue(userToken.contains("usertoken"));
        }

    }

    @Test
    public void testFullCircleWithContextTest() {
        if (!SystemTestUtil.noLocalWhydahRunning()) {

            HystrixRequestContext context = HystrixRequestContext.initializeContext();
            try {
                String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
                String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
                String userticket = UUID.randomUUID().toString();
                String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
                String userTokenId = UserXpathHelper.getUserTokenId(userToken);
                assertTrue(new CommandValidateUsertokenId(tokenServiceUri, myApplicationTokenID, userTokenId).execute());
                String userToken2 = new CommandGetUsertokenByUserticket(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userticket).execute();
                assertEquals(userToken, userToken2);
            } finally {
                context.shutdown();
            }

        }
    }


    private void testFullCircleWithContext() throws Exception {
            HystrixRequestContext context = HystrixRequestContext.initializeContext();
            try {
                String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
                String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
                String userticket = UUID.randomUUID().toString();
                String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
                String userTokenId = UserXpathHelper.getUserTokenId(userToken);
                if (!new CommandValidateUsertokenId(tokenServiceUri, myApplicationTokenID, userTokenId).execute()){
                    throw new ExecutionException("",null);
                }
                String userToken2 = new CommandGetUsertokenByUserticket(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userticket).execute();
//                assertEquals(userToken, userToken2);
            } finally {
                context.shutdown();
            }


    }

    @Ignore   // This is a longlivity test, so set as Ignore to be run manually from time to time
    @Test
    public void testRegressionFullCircleWithContext() {
        if (!SystemTestUtil.noLocalWhydahRunning()) {
            int successFull = 0;
            int nonSuccessFull = 0;
            for (int n = 0; n < 100; n++) {
                try {
                    testFullCircleWithContext();
                    successFull++;
                } catch (Exception e) {
                    nonSuccessFull++;
                }
            }
            System.out.println("Regression result:  success:" + successFull + " Failures: " + nonSuccessFull);

        }
    }
}