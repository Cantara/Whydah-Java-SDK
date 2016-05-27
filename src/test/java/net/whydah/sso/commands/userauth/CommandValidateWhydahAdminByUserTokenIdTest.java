package net.whydah.sso.commands.userauth;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandValidateWhydahAdminByUserTokenIdTest {

    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Ignore // Need whydah 2.2-alpha SDK to run against
    @Test
    public void testCommandValidateWhydahAdminByUserTokenIdTest() throws Exception {

        if (config.isSystemTestEnabled()) {
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            System.out.println(myAppTokenXml);
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            System.out.println(myApplicationTokenID);

            assertTrue(myApplicationTokenID.length() > 6);


            UserCredential userCredential = new UserCredential();
            userCredential.setUserName("useradmin");
            userCredential.setPassword("useradmin42");
            String userTokenXml = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential).execute();
            UserToken userToken = UserTokenMapper.fromUserTokenXml(userTokenXml);

            assertTrue(new CommandValidateWhydahAdminByUserTokenId(config.tokenServiceUri, myApplicationTokenID, userToken.getTokenid()).execute());

        }


    }


}

