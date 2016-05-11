package net.whydah.sso.commands.adminapi.user.role;

import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.user.mappers.UserRoleMapper;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import net.whydah.sso.user.types.UserToken;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class CommandGetUserRolesTest {


    public static SystemTestBaseConfig config;


    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testListUserRoles() throws Exception {

        if (config.isSystemTestEnabled()) {

            UserToken adminUser = config.logOnSystemTestApplicationAndSystemTestUser();
            String userRolesJson = new CommandGetUserRoles(config.userAdminServiceUri, config.myApplicationToken.getApplicationTokenId(), adminUser.getTokenid(), adminUser.getUid()).execute();
            System.out.println("Roles returned:" + userRolesJson);
            List<UserApplicationRoleEntry> roles = UserRoleMapper.fromJsonAsList(userRolesJson);
            System.out.println("Last role" + roles.listIterator().next());

        }
    }


    //  @Path("/{uid}/roles")
}
