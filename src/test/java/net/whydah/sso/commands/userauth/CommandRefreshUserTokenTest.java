package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.adminapi.user.role.CommandAddUserRole;
import net.whydah.sso.commands.adminapi.user.role.CommandGetUserRoles;
import net.whydah.sso.commands.adminapi.user.role.CommandUpdateUserRole;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.user.mappers.UserRoleMapper;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import net.whydah.sso.user.types.UserIdentity;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class CommandRefreshUserTokenTest {

    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
        //config.setLocalTest();
    }


    private UserIdentity getTestNewUserIdentity() {
        Random rand = new Random();
        rand.setSeed(new java.util.Date().getTime());
        UserIdentity user = new UserIdentity("TestUser-" + UUID.randomUUID().toString().replace("-", "").replace("_", "").substring(1, 10), "Mt Test", "Testesen", "0", UUID.randomUUID().toString().replace("-", "").replace("_", "").substring(1, 10) + "@getwhydah.com", "47" + Integer.toString(rand.nextInt(100000000)));
        return user;

    }
    
    
    private void testUpdateRoleAndRefreshToken(UserToken userToken ){
    	
    	
		String rolesJson = new CommandGetUserRoles(config.userAdminServiceUri, config.myApplicationTokenID, userToken.getTokenid(), userToken.getUid()).execute();
		List<UserApplicationRoleEntry> appRoleEntryList = UserRoleMapper.fromJsonAsList(rolesJson);
		
		UserApplicationRoleEntry foundEntry=null;
		for(UserApplicationRoleEntry entry:appRoleEntryList){
			if(entry.getRoleName().equalsIgnoreCase("INNData")){
				foundEntry = entry;
				break;
			}
		}
		
		String roleVaue = "address-" + UUID.randomUUID();
		String roleUpdateResult=null;
		if(foundEntry==null){
			//add new role INNData
			UserApplicationRoleEntry userRole = new UserApplicationRoleEntry(userToken.getUserName(), "101", "app_name", "organisation", "INNData", roleVaue);
			roleUpdateResult = new CommandAddUserRole(config.userAdminServiceUri, config.myApplicationTokenID, userToken.getTokenid(), userToken.getUid(), userRole.toJson()).execute();
		} else {
			//update new role
			foundEntry.setRoleName("INNData");
			foundEntry.setRoleValue(roleVaue);
			roleUpdateResult = new CommandUpdateUserRole(config.userAdminServiceUri, config.myApplicationTokenID, userToken.getTokenid(), userToken.getUid(), foundEntry.getId(), foundEntry.toJson()).execute();
			
		}
		//assertTrue(roleUpdateResult!=null);


		//have a new role?
		String rolesJson2 = new CommandGetUserRoles(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), userToken.getTokenid(), userToken.getUid()).execute();
		List<UserApplicationRoleEntry> appRoleEntryList2 = UserRoleMapper.fromJsonAsList(rolesJson2);
		boolean found = false;
		for(UserApplicationRoleEntry entry:appRoleEntryList2){
			if(entry.getRoleName().equalsIgnoreCase("INNData") && entry.getRoleValue().equalsIgnoreCase(roleVaue)){
				found = true;
				break;
			}
		}
		assertTrue(found);
		
		

		//refresh failed here
		String tokenXml = new CommandRefreshUserToken(config.tokenServiceUri, config.myApplicationToken.getApplicationTokenId(), config.myApplicationTokenID, userToken.getTokenid()).execute();
		assertTrue(tokenXml != null);
		//UserToken refreshUserToken = UserTokenMapper.fromUserTokenXml(tokenXml);
		//assertTrue(refreshUserToken!=null);
		
        
    }
    
    @Test
    public void testCommandRefreshUserToken() throws Exception {

        if (config.isSystemTestEnabled()) {
        	
        	
        	//admin logon
        	UserToken adminUser = config.logOnSystemTestApplicationAndSystemTestUser();
        
        	//add and activate user
//        	UserIdentity uir = getTestNewUserIdentity();
//        	String userIdentityJson = UserIdentityMapper.toJsonWithoutUID(uir);
//        	String ticket = "67678687";
//        	String phoneNo = "98765433";
//        	String pin = "3434";
//        	new CommandSendSmsPin(config.tokenServiceUri, config.myApplicationToken.getApplicationTokenId(), ApplicationTokenMapper.toXML(config.myApplicationToken), phoneNo, pin).execute();
//        	String userAddRoleResult = new CommandCreatePinVerifiedUser(config.tokenServiceUri, config.myApplicationToken.getApplicationTokenId(), ApplicationTokenMapper.toXML(config.myApplicationToken), adminUser.getTokenid(), ticket, phoneNo, pin, userIdentityJson).execute();
//        	System.out.println("testAddUser:" + userAddRoleResult);
//
//        	//get token
//        	UserToken foundUserToken = UserTokenMapper.fromUserTokenXml(userAddRoleResult);
        	
        	
        	testUpdateRoleAndRefreshToken(adminUser);

        }

    }
}
