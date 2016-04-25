package net.whydah.sso.commands.userauth;

import static org.junit.Assert.assertTrue;
import net.whydah.sso.application.BaseConfig;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplicationWithStubbedFallback;
import net.whydah.sso.user.helpers.UserXpathHelper;

import org.junit.BeforeClass;
import org.junit.Test;

public class CommandCheckUserTokenIdTest {
//    private static URI tokenServiceUri;
//    private static ApplicationCredential appCredential;
//    private static UserCredential userCredential;
//    private static boolean integrationMode = true;

	static BaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
//        tokenServiceUri = URI.create("https://no_host").build();
//        if (integrationMode) {
//            tokenServiceUri = URI.create("https://whydahdev.altrancloud.com/tokenservice/").build();
//        }
//        appCredential = new ApplicationCredential("15", "MyApp", "33779936R6Jr47D4Hj5R6p9qT");
//        userCredential = new UserCredential("useradmin", "useradmin42");
//
//        // HystrixCommandProperties.Setter().withFallbackEnabled(!integrationMode);
//        HystrixRequestContext context = HystrixRequestContext.initializeContext();
    	config = new BaseConfig();
    }


    @Test
    public void testApplicationLoginCommand() throws Exception {

        String myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(config.tokenServiceUri, config.appCredential).execute();
        System.out.println(myAppTokenXml);
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        System.out.println(myApplicationTokenID);

        assertTrue(myApplicationTokenID.length() > 6);


        String userToken = new CommandLogonUserByUserCredentialWithStubbedFallback(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential).execute();

        String userTokenId = UserXpathHelper.getUserTokenId(userToken);
        boolean isvalidToken = new CommandValidateUsertokenIdWithStubbedFallback(config.tokenServiceUri, myApplicationTokenID, userTokenId).execute();
        assertTrue("Error: usertokenid NOT verified successfully", isvalidToken);
    }


}

