package net.whydah.sso.usecases;

import net.whydah.sso.application.helpers.ApplicationHelper;
import net.whydah.sso.application.mappers.ApplicationMapper;
import net.whydah.sso.application.types.Application;
import net.whydah.sso.commands.adminapi.application.CommandAddApplication;
import net.whydah.sso.commands.adminapi.application.CommandListApplications;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.commands.userauth.CommandGetUsertokenByUsertokenId;
import net.whydah.sso.session.baseclasses.BaseWhydahServiceClient;
import net.whydah.sso.user.helpers.UserXpathHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AddUserRoleTest {
	static SystemTestBaseConfig config;
	static BaseWhydahServiceClient client;

	@BeforeClass
	public static void setup() throws Exception {
		config = new SystemTestBaseConfig();
		if (config.isSystemTestEnabled()) {

			client = new BaseWhydahServiceClient(config.tokenServiceUri.toString(), config.userAdminServiceUri.toString(), config.TEMPORARY_APPLICATION_ID, config.TEMPORARY_APPLICATION_NAME, config.TEMPORARY_APPLICATION_SECRET);
			client.getWAS().updateApplinks();
		}
	}

	@Test
	public void testUpdateRoleAndRefreshUserTokenWithExistingApplication(){
		if (config.isSystemTestEnabled()) {
			assertTrue(client.updateOrCreateUserApplicationRoleEntry("", "ACSResource", "WhyDah", "MyData", "welcome", config.logOnSystemTestApplicationAndSystemTestUser_getTokenXML()));
		}
	}
	
	@Test
	public void testUpdateRoleAndRefreshUserTokenWithNonExistingApplciation(){
		if (config.isSystemTestEnabled()) {
			assertFalse(client.updateOrCreateUserApplicationRoleEntry("", "NON-EXISTING", "WhyDah", "MyData", "welcome", config.logOnSystemTestApplicationAndSystemTestUser_getTokenXML()));
		}
	}
	
	@Test
	public void testUpdateRoleAndRefreshUserTokenWithNonExistingApplciation2(){
		if (config.isSystemTestEnabled()) {
			//create a new application now
		  
            String userTokenId = UserXpathHelper.getUserTokenId(config.logOnSystemTestApplicationAndSystemTestUser_getTokenXML());
            int existingApplications = countApplications(config.myApplicationTokenID, userTokenId);
            Application newApplication = ApplicationMapper.fromJson(ApplicationHelper.getDummyApplicationJson());
            String applicationJson = ApplicationMapper.toJson(newApplication);
            String testAddApplication = new CommandAddApplication(config.userAdminServiceUri, config.myApplicationTokenID, userTokenId, applicationJson).execute();
            System.out.print("new app : " + testAddApplication);
            assertTrue(existingApplications == (countApplications(config.myApplicationTokenID, userTokenId) - 1));
            
            //however, client cannot update because it is holding the old application list
			assertFalse(client.updateOrCreateUserApplicationRoleEntry(newApplication.getId(), newApplication.getName(), "WhyDah", "MyData", "welcome", config.logOnSystemTestApplicationAndSystemTestUser_getTokenXML()));
			
			//should update the application list
			client.getWAS().updateApplinks(true);
			
			//now, it is ok to update role
			assertTrue(client.updateOrCreateUserApplicationRoleEntry(newApplication.getId(), newApplication.getName(), "WhyDah", "MyData", "welcome", config.logOnSystemTestApplicationAndSystemTestUser_getTokenXML()));

			// Check for correct UserToken

			String userTokenXml2 = new CommandGetUsertokenByUsertokenId(config.tokenServiceUri, config.myApplicationTokenID, config.myAppTokenXml, userTokenId).execute();
			assertTrue(userTokenXml2.contains("MyData"));

		}
	}
	
	 private int countApplications(String myApplicationTokenID, String userTokenId) {
	        String applicationsJson = new CommandListApplications(config.userAdminServiceUri, myApplicationTokenID).execute();
	        System.out.println("applicationsJson=" + applicationsJson);
	        assertTrue(applicationsJson.length() > 100);
	        List<Application> applications = ApplicationMapper.fromJsonList(applicationsJson);
	        assertTrue(applications.size() > 2);
	        return applications.size();


	    }
}
