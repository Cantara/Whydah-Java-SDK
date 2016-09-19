package net.whydah.sso.commands.userauth;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CommandGetUsertokenByUsertokenIdTest {

    static SystemTestBaseConfig config;
    private static final Logger log = LoggerFactory.getLogger(CommandGetUsertokenByUsertokenIdTest.class);


    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testCommandGetUsertokenByUsertokenId() throws Exception {

        if (config.isSystemTestEnabled()) {
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            System.out.println(myAppTokenXml);
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            System.out.println(myApplicationTokenID);

            assertTrue(myApplicationTokenID.length() > 6);


            String userTokenXml = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential).execute();
            UserToken userToken = UserTokenMapper.fromUserTokenXml(userTokenXml);

            String userTokenXml2 = new CommandGetUsertokenByUsertokenId(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, userToken.getTokenid()).execute();

            UserToken ut2 = UserTokenMapper.fromUserTokenXml(userTokenXml2);
            log.trace("Received usertoken", ut2.toString());
            assertTrue(userToken.getFirstName().equalsIgnoreCase(ut2.getFirstName()));
            assertTrue(userToken.getCellPhone().equalsIgnoreCase(ut2.getCellPhone()));
            assertTrue(userToken.getPersonRef().equalsIgnoreCase(ut2.getPersonRef()));
            assertTrue(ut2.getPersonRef().length() > 0);  // Ensure that we get a valid personRef form our systemuser

        }


    }

    //    @Ignore
    @Test
    public void testCommandGetUsertokenByUsertokenIdRolemappings() throws Exception {

        if (config.isSystemTestEnabled()) {
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            System.out.println(myAppTokenXml);
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            System.out.println(myApplicationTokenID);

            assertTrue(myApplicationTokenID.length() > 6);


            String userTokenXml = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential).execute();
            UserToken userToken = UserTokenMapper.fromUserTokenXml(userTokenXml);

            String userTokenXml2 = new CommandGetUsertokenByUsertokenId(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, userToken.getTokenid()).execute();
            log.debug(userTokenXml2);

            UserToken ut2 = UserTokenMapper.fromUserTokenXml(userTokenXml2);
            for (UserApplicationRoleEntry role : ut2.getRoleList()) {
                log.debug("Checking for adminrole user UID:{} roleName: {} ", userToken.getUid(), role.getRoleName());
                assertNotNull(role.getApplicationId());
                assertNotNull(role.getApplicationName());
                assertNotNull(role.getOrgName());
                assertNotNull(role.getRoleName());
            }

        }


    }

}