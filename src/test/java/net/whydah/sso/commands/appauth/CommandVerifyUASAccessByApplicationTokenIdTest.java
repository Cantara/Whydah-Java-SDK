package net.whydah.sso.commands.appauth;

import static org.junit.Assert.assertTrue;
import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.commands.adminapi.application.CommandListApplications;
import net.whydah.sso.util.SystemTestBaseConfig;

import org.junit.BeforeClass;
import org.junit.Test;

public class CommandVerifyUASAccessByApplicationTokenIdTest {

	static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testCommandValidateApplicationTokenIdTest() throws Exception {
    	
        if (config.isSystemTestEnabled()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
            assertTrue(myAppTokenXml != null);
            assertTrue(myAppTokenXml.length() > 6);
            ApplicationToken at = ApplicationTokenMapper.fromXml(myAppTokenXml);
            boolean hasAccess = new CommandVerifyUASAccessByApplicationTokenId(config.userAdminServiceUri.toString(), at.getApplicationTokenId()).execute();
            if(hasAccess){
            	String appjson = new CommandListApplications(config.userAdminServiceUri, at.getApplicationTokenId()).execute();
            	assertTrue(appjson!=null);
            }
          
        }
    }

}
