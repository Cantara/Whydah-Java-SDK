package net.whydah.sso.commands;

import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserXpathHelper;
import net.whydah.sso.util.WhydahUtil;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by totto on 12/2/14.
 */
public class TestCommandLogonUserByUserCredential {

    private static URI tokenServiceUri;
    private static ApplicationCredential appCredential;
    private static UserCredential userCredential;
    private static boolean integrationMode = true;


    @BeforeClass
    public static void setup() throws Exception {
        tokenServiceUri = UriBuilder.fromUri("https://whydahdev.altrancloud.com/tokenservice/").build();
        appCredential = new ApplicationCredential();
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
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppToken(myAppTokenXml);
        System.out.println(myApplicationTokenID);

        assertTrue(myApplicationTokenID.length() > 6);

        String userticket = UUID.randomUUID().toString();

        myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(tokenServiceUri, appCredential).execute();
        myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppToken(myAppTokenXml);
        String userToken = new CommandLogonUserByUserCredentialWithStubbedFallback(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
        String userTokenId = UserXpathHelper.getUserTokenId(userToken);
        if (integrationMode) {
            assertTrue(new CommandValidateUsertokenId(tokenServiceUri, myApplicationTokenID, userTokenId).execute());
        }

        myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(tokenServiceUri, appCredential).execute();
        myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppToken(myAppTokenXml);
        String userToken2 = new CommandGetUsertokenByUserticket(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userticket).execute();


    }

    @Test
    public void tesLlogOnApplicationAndUser() throws Exception {

        String userToken = WhydahUtil.logOnApplicationAndUser(tokenServiceUri.toString(), appCredential.getApplicationID(), appCredential.getApplicationSecret(), userCredential.getUserName(), userCredential.getPassword());

    }

    @Test
    public void testFullCircleWithContextTest() {
            HystrixRequestContext context = HystrixRequestContext.initializeContext();
            try {
                String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
                String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppToken(myAppTokenXml);
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

    private void testFullCircleWithContext() throws Exception {
            HystrixRequestContext context = HystrixRequestContext.initializeContext();
            try {
                String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
                String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppToken(myAppTokenXml);
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

    @Test
    public void testRegressionFullCircleWithContext() {
        int successFull=0;
        int nonSuccessFull=0;
        for (int n = 0; n < 100; n++) {
            try {
                testFullCircleWithContext();
                successFull++;
            } catch (Exception e){
                nonSuccessFull++;
            }
        }
        System.out.println("Regression result:  success:"+successFull+" Failures: "+nonSuccessFull);

    }
}
