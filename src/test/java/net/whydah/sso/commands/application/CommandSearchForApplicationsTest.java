package net.whydah.sso.commands.application;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class CommandSearchForApplicationsTest {
    private final static Logger log = LoggerFactory.getLogger(CommandSearchForApplicationsTest.class);
    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }

    @Test
    public void testCommandSearchForApplications() throws Exception {
        if (config.isSystemTestEnabled()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
           
            boolean isValid = true;
            try{
            	new ApplicationTokenID(myApplicationTokenID);
            }catch(Exception ex){
            	isValid = false;
            }
            assertTrue(isValid);
            
            String applications = new CommandSearchForApplications(config.userAdminServiceUri, myApplicationTokenID, "100").execute();
            assertTrue(applications != null);

        }
    }
}
