package net.whydah.sso.commands.userauth;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.util.WhydahUtil;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class CommandLogonUserByUserCredentialTest {


    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {

        config = new SystemTestBaseConfig();

    }


    @Test
    public void testCommandGetUsertokenByUserticket() throws Exception {

        if (config.isSystemTestEnabled()) {
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            System.out.println(myAppTokenXml);
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            System.out.println(myApplicationTokenID);

            assertTrue(myApplicationTokenID.length() > 6);

            String userticket = UUID.randomUUID().toString();

            myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);

            assertTrue(new CommandValidateUsertokenId(config.tokenServiceUri, myApplicationTokenID, userTokenId).execute());

            myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            String userToken2 = new CommandGetUsertokenByUserticket(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, userticket).execute();

        }



    }

    @Test
    public void tesLogOnApplicationAndUser() throws Exception {

        if (config.isSystemTestEnabled()) {
            String userToken = WhydahUtil.logOnApplicationAndUser(config.tokenServiceUri.toString(), config.appCredential.getApplicationID(), "", config.appCredential.getApplicationSecret(), config.userCredential.getUserName(), config.userCredential.getPassword());
            assertNotNull(userToken);
            assertTrue(userToken.contains("usertoken"));
        }

    }

    @Test
    public void testFullCircleWithContextTest() {
        if (config.isSystemTestEnabled()) {

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
        if (config.isSystemTestEnabled()) {
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