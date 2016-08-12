package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Test;

public class CommandResetUserPasswordTest {

    private static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testCommandGetUser() throws Exception {


        if (config.isSystemTestEnabled()) {
            UserToken adminUser = config.logOnSystemTestApplicationAndSystemTestUser();


            boolean result = new CommandResetUserPassword(config.userAdminServiceUri, config.myApplicationTokenID, "username").execute();
            System.out.println("CommandResetUserPassword return=" + result);

            result = new CommandResetUserPassword(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), "username", "NewUserPasswordResetEmail.ftl").execute();
            System.out.println("CommandResetUserPassword return=" + result);

        }


    }

}