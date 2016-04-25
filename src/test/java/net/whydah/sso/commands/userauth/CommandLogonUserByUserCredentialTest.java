package net.whydah.sso.commands.userauth;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import net.whydah.sso.application.BaseConfig;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.appauth.CommandLogonApplicationWithStubbedFallback;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.util.WhydahUtil;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class CommandLogonUserByUserCredentialTest {

//    private static URI tokenServiceUri;
//    private static ApplicationCredential appCredential;
//    private static UserCredential userCredential;
//    private static boolean integrationMode = false;

	static BaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
//        tokenServiceUri = URI.create("http://localhost:9998/tokenservice").build();
//        if (integrationMode) {
//            tokenServiceUri = URI.create("https://whydahdev.altrancloud.com/tokenservice/").build();
//        }
//        appCredential = new ApplicationCredential("11", "my app", "6r46g3q986Ep6By7B9J46m96D");
//        userCredential = new UserCredential("admin", "whydahadmin");
//
//        // HystrixCommandProperties.Setter().withFallbackEnabled(!integrationMode);
//        HystrixRequestContext context = HystrixRequestContext.initializeContext();
    	
    	config = new BaseConfig();

    }


    @Ignore
    @Test
    public void testApplicationLoginCommand() throws Exception {

        String myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(config.tokenServiceUri, config.appCredential).execute();
        System.out.println(myAppTokenXml);
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        System.out.println(myApplicationTokenID);

        assertTrue(myApplicationTokenID.length() > 6);

        String userticket = UUID.randomUUID().toString();

        myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(config.tokenServiceUri, config.appCredential).execute();
        myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        String userToken = new CommandLogonUserByUserCredentialWithStubbedFallback(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
        String userTokenId = UserXpathHelper.getUserTokenId(userToken);
        if (config.systemTest) {
            assertTrue(new CommandValidateUsertokenId(config.tokenServiceUri, myApplicationTokenID, userTokenId).execute());
        }

        myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(config.tokenServiceUri, config.appCredential).execute();
        myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        String userToken2 = new CommandGetUsertokenByUserticket(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, userticket).execute();


    }

    @Ignore
    @Test
    public void tesLogOnApplicationAndUser() throws Exception {

        if (config.systemTest) {
            String userToken = WhydahUtil.logOnApplicationAndUser(config.tokenServiceUri.toString(), config.appCredential.getApplicationID(), "", config.appCredential.getApplicationSecret(), config.userCredential.getUserName(), config.userCredential.getPassword());
            assertNotNull(userToken);
            assertTrue(userToken.contains("usertoken"));
        }

    }

    @Test
    public void testFullCircleWithContextTest() {
        if (config.enableTesting()) {

            HystrixRequestContext context = HystrixRequestContext.initializeContext();
            try {
                String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
                String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
                String userticket = UUID.randomUUID().toString();
                String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
                String userTokenId = UserXpathHelper.getUserTokenId(userToken);
                assertTrue(new CommandValidateUsertokenId(config.tokenServiceUri, myApplicationTokenID, userTokenId).execute());
                String userToken2 = new CommandGetUsertokenByUserticket(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, userticket).execute();
                assertEquals(userToken, userToken2);
            } finally {
                context.shutdown();
            }

        }
    }


    private void testFullCircleWithContext() throws Exception {
            HystrixRequestContext context = HystrixRequestContext.initializeContext();
            try {
                String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
                String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
                String userticket = UUID.randomUUID().toString();
                String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
                String userTokenId = UserXpathHelper.getUserTokenId(userToken);
                if (!new CommandValidateUsertokenId(config.tokenServiceUri, myApplicationTokenID, userTokenId).execute()){
                    throw new ExecutionException("",null);
                }
                String userToken2 = new CommandGetUsertokenByUserticket(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, userticket).execute();
//                assertEquals(userToken, userToken2);
            } finally {
                context.shutdown();
            }


    }

    @Ignore   // This is a longlivity test, so set as Ignore to be run manually from time to time
    @Test
    public void testRegressionFullCircleWithContext() {
        if (config.enableTesting()) {
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