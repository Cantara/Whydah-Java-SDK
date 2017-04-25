package net.whydah.sso.commands.adminapi.user;

import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.UUID;

import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.commands.userauth.CommandGetUsertokenByUserticket;
import net.whydah.sso.commands.userauth.CommandGetUsertokenByUsertokenId;
import net.whydah.sso.commands.userauth.CommandRefreshUserToken;
import net.whydah.sso.commands.userauth.CommandRefreshUserTokenTest;
import net.whydah.sso.user.mappers.UserIdentityMapper;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.user.types.UserIdentity;
import net.whydah.sso.user.types.UserToken;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ch.qos.logback.core.db.dialect.MySQLDialect;

public class CommandUpdateUserTest {
	  static SystemTestBaseConfig config;

	    @BeforeClass
	    public static void setup() throws Exception {
	        config = new SystemTestBaseConfig();
	    }

	    //wait until UAS is updated
	    @Test
	    @Ignore
	    public void testUpdateUser() throws Exception {
	        if (config.isSystemTestEnabled()) {

	        	
	        	config.logOnSystemTestApplication();
	        	String userTicket1 = UUID.randomUUID().toString();
	        	String userTicket2 = UUID.randomUUID().toString();
	            UserToken adminUser = UserTokenMapper.fromUserTokenXml(config.logOnBySystemTestUserCredential(userTicket1));
	            UserToken normalUser = UserTokenMapper.fromUserTokenXml(config.logOnByUserCredential(userTicket2, new UserCredential(config.userName2, config.password2)));
	            
	            String userIdentityJson = new CommandGetUser(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUser.getTokenid(), normalUser.getUid()).execute();
	            System.out.println("getuser:" + userIdentityJson);
	   
	           
	            UserIdentity updateMe = UserIdentityMapper.fromUserIdentityJson(userIdentityJson);
	           //keep the current attributes
	            String oldFirstName = updateMe.getFirstName();
	            String oldLastName = updateMe.getLastName();
	            String oldCellPhone = updateMe.getCellPhone();
	            String oldEmail = updateMe.getEmail();
	            String oldPerRef= updateMe.getPersonRef();
	            
	            //try updating
	            updateMe.setFirstName((updateMe.getFirstName().contains("_")? updateMe.getFirstName().substring(0, updateMe.getFirstName().indexOf('_')) : updateMe.getFirstName()) + "_" + userTicket2);
	            updateMe.setLastName((updateMe.getLastName().contains("_")? updateMe.getLastName().substring(0, updateMe.getLastName().indexOf('_')) : updateMe.getLastName()) + "_" + userTicket2);
	            updateMe.setCellPhone("99999999");
	            updateMe.setEmail("test@whydah.com");
	            updateMe.setPersonRef((updateMe.getPersonRef().contains("_")? updateMe.getPersonRef().substring(0, updateMe.getPersonRef().indexOf('_')) : updateMe.getPersonRef()) + "_" + userTicket2);
	            
	            //do update
	            String userUpdateResult = new CommandUpdateUser(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUser.getTokenid(), updateMe.getUsername(), UserIdentityMapper.toJson(updateMe)).execute();
	            System.out.println("update=" + userUpdateResult);
	            UserIdentity updated = UserIdentityMapper.fromUserIdentityJson(userUpdateResult);
	            //check for modifications
	            assertTrue(updated.getFirstName().equals(updateMe.getFirstName()));
	            assertTrue(updated.getCellPhone().equals(updateMe.getCellPhone()));
	            assertTrue(updated.getEmail().equals(updateMe.getEmail()));
	            assertTrue(updated.getLastName().equals(updateMe.getLastName()));
	            assertTrue(updated.getPersonRef().equals(updateMe.getPersonRef()));
	            
	            //NO NEED TO CALL REFRESH HERE ANY MORE, IT IS AUTOMATICALLY UPDATE WHEN GETTING A USERTOKEN
//	            String tokenXml = new CommandRefreshUserToken(config.tokenServiceUri, config.myApplicationToken.getApplicationTokenId(), config.myApplicationTokenID, normalUser.getTokenid()).execute();
//	    		assertTrue(tokenXml != null);
	            
	            //check if UserToken info changed in STS
	            String utXml = new CommandGetUsertokenByUserticket(config.tokenServiceUri, config.myApplicationToken.getApplicationTokenId(), config.myAppTokenXml, userTicket2).execute();
	            UserToken ut = UserTokenMapper.fromUserTokenXml(utXml);
	            assertTrue(ut.getFirstName().contains(userTicket2));
	            assertTrue(ut.getFirstName().equals(updateMe.getFirstName()));
	            assertTrue(ut.getLastName().contains(userTicket2));
	            assertTrue(ut.getLastName().equals(updateMe.getLastName()));
	            assertTrue(ut.getCellPhone().equals("99999999"));
	            assertTrue(ut.getEmail().equals("test@whydah.com"));
	            assertTrue(ut.getPersonRef().contains(userTicket2));
	            assertTrue(ut.getPersonRef().equals(updateMe.getPersonRef()));
	            //Just get the old attributes back

	            updateMe.setFirstName(oldFirstName);
	            updateMe.setLastName(oldLastName);
	            updateMe.setCellPhone(oldCellPhone);
	            updateMe.setEmail(oldEmail);
	            updateMe.setPersonRef(oldPerRef);
	            
	            //do update
	            new CommandUpdateUser(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUser.getTokenid(), updateMe.getUsername(), UserIdentityMapper.toJson(updateMe)).execute();
	            
	        }

	    }

}
