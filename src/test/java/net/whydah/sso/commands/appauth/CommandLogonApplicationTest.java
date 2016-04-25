package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.BaseConfig;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import rx.Observable;

import java.util.concurrent.Future;

import static org.junit.Assert.assertTrue;


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

    @Ignore  // temp ignore
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


}
