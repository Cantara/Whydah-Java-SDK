package net.whydah.sso.commands.adminapi.user;


import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommandCheckIfUserExistsTest {


    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testCommandCheckIfUserExistsTest() throws Exception {

        if (config.isSystemTestEnabled()) {
            UserToken adminUser = config.logOnSystemTestApplicationAndSystemTestUser();

            Boolean userExists = new CommandUserExists(config.userAdminServiceUri, config.myApplicationTokenID, adminUser.getTokenid(), "useradmin").execute();
            // Need to create real testdata for the assert to work, as systemusers does not have the roles set..
            assertTrue(userExists);

            Boolean userExists2 = new CommandUserExists(config.userAdminServiceUri, config.myApplicationTokenID, adminUser.getTokenid(), "NonExistingUser").execute();
            // Need to create real testdata for the assert to work, as systemusers does not have the roles set..
            assertFalse(userExists2);

        }
    }

}
