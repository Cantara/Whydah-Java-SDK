package net.whydah.sso.commands.appauth;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationHelper;
import org.junit.BeforeClass;
import org.junit.Test;
import rx.Observable;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

/**
 * Created by totto on 12/2/14.
 */
public class CommandLogonApplicationTest {

    private static URI tokenServiceUri;
    private static ApplicationCredential appCredential;
    private static boolean integrationMode = false;



    @BeforeClass
    public static void setup() throws Exception {
        appCredential = new ApplicationCredential("15","33779936R6Jr47D4Hj5R6p9qT");
        tokenServiceUri = UriBuilder.fromUri("https://no_host").build();
        if (integrationMode) {
            tokenServiceUri = UriBuilder.fromUri("https://whydahdev.altrancloud.com/tokenservice/").build();
        }

    }


    @Test
    public void testApplicationLoginCommandFallback() throws Exception {

        appCredential=new ApplicationCredential("15","false secret");

        String myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(tokenServiceUri, appCredential).execute();
        // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
        assertEquals(ApplicationHelper.getDummyApplicationToken(), myAppTokenXml);

        Future<String> fmyAppTokenXml = new CommandLogonApplicationWithStubbedFallback(tokenServiceUri, appCredential).queue();
        assertEquals(ApplicationHelper.getDummyApplicationToken(), fmyAppTokenXml.get());


        Observable<String> omyAppTokenXml = new CommandLogonApplicationWithStubbedFallback(tokenServiceUri, appCredential).observe();
        // blocking
        assertEquals(ApplicationHelper.getDummyApplicationToken(), omyAppTokenXml.toBlocking().single());
    }

    @Test
    public void testApplicationLoginCommand() throws Exception {

        String myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(tokenServiceUri, appCredential).execute();
        // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
        assertTrue(myAppTokenXml!=null);
        assertTrue(myAppTokenXml.length() > 6);

        Future<String> fmyAppTokenXml = new CommandLogonApplicationWithStubbedFallback(tokenServiceUri, appCredential).queue();
        assertTrue(fmyAppTokenXml.get().length() > 6);

        Observable<String> omyAppTokenXml = new CommandLogonApplicationWithStubbedFallback(tokenServiceUri, appCredential).observe();
        // blocking
        assertTrue(omyAppTokenXml.toBlocking().single().length() > 6);
    }

    @Test
    public void testWithCacheHits() {
        if (integrationMode){
            HystrixRequestContext context = HystrixRequestContext.initializeContext();
            try {
                CommandLogonApplication command2a = new CommandLogonApplication(tokenServiceUri, appCredential);
                CommandLogonApplication command2b = new CommandLogonApplication(tokenServiceUri, appCredential);

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
                CommandLogonApplication command3b = new CommandLogonApplication(tokenServiceUri, appCredential);
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
