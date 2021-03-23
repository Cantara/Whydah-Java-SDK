package net.whydah.sso.commands.adminapi.user;


import net.whydah.sso.user.types.UserToken;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommandUIDExistsTest {


    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testCommandCheckIfUserExistsTest() throws Exception {

        if (config.isSystemTestEnabled()) {
            UserToken adminUser = config.logOnSystemTestApplicationAndSystemTestUser();

            Boolean userExists = new CommandUIDExists(config.userAdminServiceUri, config.myApplicationTokenID, adminUser.getUserTokenId(), "useradmin").execute();
            // Need to create real testdata for the assert to work, as systemusers does not have the roles set..
            assertTrue(userExists);

            Boolean userExists2 = new CommandUIDExists(config.userAdminServiceUri, config.myApplicationTokenID, adminUser.getUserTokenId(), "NonExistingUser").execute();
            // Need to create real testdata for the assert to work, as systemusers does not have the roles set..
            assertFalse(userExists2);

        }
    }

}
