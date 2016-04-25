package net.whydah.sso.commands.appauth;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import net.whydah.sso.application.BaseConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import rx.Observable;

import java.util.concurrent.Future;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by totto on 12/2/14.
 */
public class CommandLogonApplicationTest {

    static BaseConfig config;
    private static boolean integrationMode = false;

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


    @Test
    public void testApplicationLoginCommandFallback() throws Exception {

        if (config.enableTesting()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
            //assertEquals(ApplicationHelper.getDummyApplicationToken(), myAppTokenXml);

            Future<String> fmyAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).queue();
            //assertEquals(ApplicationHelper.getDummyApplicationToken(), fmyAppTokenXml.get());


            Observable<String> omyAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).observe();
            // blocking
            //assertEquals(ApplicationHelper.getDummyApplicationToken(), omyAppTokenXml.toBlocking().single());
        }
    }

    @Test
    public void testApplicationLoginCommand() throws Exception {

        if (config.enableTesting()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
            assertTrue(myAppTokenXml != null);
            assertTrue(myAppTokenXml.length() > 6);

            Future<String> fmyAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).queue();
            assertTrue(fmyAppTokenXml.get().length() > 6);

            Observable<String> omyAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).observe();
            // blocking
            assertTrue(omyAppTokenXml.toBlocking().single().length() > 6);
        }
    }

    @Test
    public void testWithCacheHits() {
        if (integrationMode){
            HystrixRequestContext context = HystrixRequestContext.initializeContext();
            try {
                CommandLogonApplication command2a = new CommandLogonApplication(config.tokenServiceUri, config.appCredential);
                CommandLogonApplication command2b = new CommandLogonApplication(config.tokenServiceUri, config.appCredential);

                assertTrue(command2a.execute()!=null);
                // this is the first time we've executed this command with
                // the value of "2" so it should not be from cache
                assertFalse(command2a.isResponseFromCache());

                assertTrue(command2b.execute()!=null);
                // this is the second time we've executed this command with
                // the same value so it should return from cache
                assertTrue(command2b.isResponseFromCache());
            } finally {
                context.shutdown();
            }

            // start a new request context
            context = HystrixRequestContext.initializeContext();
            try {
                CommandLogonApplication command3b = new CommandLogonApplication(config.tokenServiceUri, config.appCredential);
                assertTrue(command3b.execute()!=null);
                // this is a new request context so this
                // should not come from cache
                assertFalse(command3b.isResponseFromCache());
            } finally {
                context.shutdown();
            }

        }
    }

}
