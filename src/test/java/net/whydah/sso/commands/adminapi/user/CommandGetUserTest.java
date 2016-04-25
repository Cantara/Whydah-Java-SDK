package net.whydah.sso.commands.adminapi.user;

import static org.junit.Assert.assertTrue;

import java.util.UUID;

import net.whydah.sso.application.BaseConfig;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.appauth.CommandLogonApplicationWithStubbedFallback;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredentialWithStubbedFallback;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserToken;

import org.junit.BeforeClass;
import org.junit.Test;

public class CommandGetUserTest{

	static //    private static URI tokenServiceUri;
	//    private static ApplicationCredential appCredential;
	//    private static UserCredential userCredential;
	//    private static boolean systemtest = false;
	//    private static URI userAdminServiceUri;

	BaseConfig config;

	@BeforeClass
	public static void setup() throws Exception {
		config = new BaseConfig();
		//        appCredential = new ApplicationCredential("15", "TempApp", "HK8fGpWmK66ckWaEVn3tF9fRK");
		//        tokenServiceUri = URI.create("https://no_host").build();
		//        userCredential = new UserCredential("useradmin", "useradmin42");
		//
		//        userAdminServiceUri = URI.create("https://no_host").build();
		//
		//        if (systemtest) {
		//            tokenServiceUri = URI.create("https://whydahdev.cantara.no/tokenservice/").build();
		//            userAdminServiceUri = URI.create("https://whydahdev.cantara.no/useradminservice/").build();
		//        }
		//        SSLTool.disableCertificateValidation();
	}


	//    @Test
	//    public void testCommandGetUser() throws Exception {
	//
	//        String myAppTokenXml;
	//        if (systemtest) {
	//            myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
	//        } else {
	//            myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(tokenServiceUri, appCredential).execute();
	//        }
	//        System.out.println("myAppTokenXml:" + myAppTokenXml);
	//        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
	//        assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
	//        String userticket = UUID.randomUUID().toString();
	//
	//        String userToken;
	//        if (systemtest) {
	//            userToken = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
	//        } else {
	//            userToken = new CommandLogonUserByUserCredentialWithStubbedFallback(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
	//        }
	//        String userTokenId = UserXpathHelper.getUserTokenId(userToken);
	//        assertTrue(userTokenId != null && userTokenId.length() > 5);
	//
	//        String userAggregateJson;
	//        if (systemtest) {
	//            userAggregateJson = new CommandGetUser(userAdminServiceUri, myApplicationTokenID, userTokenId, "useradmin").execute();
	//            System.out.println("userAggregateJson=" + userAggregateJson);
	//            UserToken foundUserToken = UserTokenMapper.fromUserAggregateJson(userAggregateJson);
	//            System.out.println(foundUserToken.toString());
	//        }
	//
	//
	//    }

	@Test
	public void testCommandGetUser() throws Exception {




		boolean systemtest = config.enableTesting();
		String myAppTokenXml;
		if (systemtest) {
			myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
		} else {
			myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(config.tokenServiceUri, config.appCredential).execute();
		}

		String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
		assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
		String userticket = UUID.randomUUID().toString();
		String userToken;
		if (systemtest) {
			userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
		} else {
			userToken = new CommandLogonUserByUserCredentialWithStubbedFallback(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
		}
		String userTokenId = UserXpathHelper.getUserTokenId(userToken);
		assertTrue(userTokenId != null && userTokenId.length() > 5);


		String userAggregateJson;
		if (systemtest) {
			userAggregateJson = new CommandGetUser(config.userAdminServiceUri, myApplicationTokenID, userTokenId, "useradmin").execute();
			System.out.println("userAggregateJson=" + userAggregateJson);
			UserToken foundUserToken = UserTokenMapper.fromUserAggregateJson(userAggregateJson);
			System.out.println(foundUserToken.toString());
		}


	}

}