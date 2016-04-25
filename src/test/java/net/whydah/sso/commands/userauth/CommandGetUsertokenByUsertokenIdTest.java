package net.whydah.sso.commands.userauth;

import net.whydah.sso.application.SystemTestBaseConfig;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandGetUsertokenByUsertokenIdTest {

    static SystemTestBaseConfig config;

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
            assertTrue(userToken.getFirstName().equalsIgnoreCase(ut2.getFirstName()));
            assertTrue(userToken.getCellPhone().equalsIgnoreCase(ut2.getCellPhone()));

        }


    }


}