package net.whydah.sso.commands.appauth;

import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import rx.Observable;

import java.util.concurrent.Future;

import static org.junit.Assert.assertTrue;


public class CommandLogonApplicationTest {

    static SystemTestBaseConfig config;
    private static boolean integrationMode = false;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
//        appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
//        tokenServiceUri = URI.create(userTokenService).build();
//        userCredential = new UserCredential(userName, password);
//
//        userAdminServiceUri = URI.create(userAdminService).build();
//
//        if (systemTest) {
//            tokenServiceUri = URI.create("https://whydahdev.cantara.no/tokenservice/").build();
//            userAdminServiceUri = URI.create("https://whydahdev.cantara.no/tokenservice/").build();
//        }
//        SSLTool.disableCertificateValidation();
    }


    @Ignore
    @Test
    public void testApplicationLoginCommandFallback() throws Exception {

        if (config.isSystemTestEnabled()) {

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

        if (config.isSystemTestEnabled()) {

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
