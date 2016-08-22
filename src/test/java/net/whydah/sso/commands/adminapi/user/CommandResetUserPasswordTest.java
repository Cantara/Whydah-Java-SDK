package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class CommandResetUserPasswordTest {

    private static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    @Ignore
    public void testCommandResetUserPassword() throws Exception {


        if (config.isSystemTestEnabled()) {
            UserToken adminUser = config.logOnSystemTestApplicationAndSystemTestUser();

            boolean result = new CommandResetUserPassword(config.userAdminServiceUri, config.myApplicationTokenID, config.userName).execute();
            System.out.println("CommandResetUserPassword return=" + result);

            Thread.sleep(5000);
            result = new CommandResetUserPassword(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), config.userName, "NewUserPasswordResetEmail.ftl").execute();
            System.out.println("CommandResetUserPassword return=" + result);

        }


    }

}