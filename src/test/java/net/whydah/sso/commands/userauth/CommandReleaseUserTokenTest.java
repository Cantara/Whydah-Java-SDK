package net.whydah.sso.commands.userauth;

import net.whydah.sso.user.types.UserIdentity;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.junit.BeforeClass;

import java.util.Random;
import java.util.UUID;

public class CommandReleaseUserTokenTest {

    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    private UserIdentity getTestNewUserIdentity() {
        Random rand = new Random();
        rand.setSeed(new java.util.Date().getTime());
        UserIdentity user = new UserIdentity("TestUser-" + UUID.randomUUID().toString().replace("-", "").replace("_", "").substring(1, 10), "Mt Test", "Testesen", "0", UUID.randomUUID().toString().replace("-", "").replace("_", "").substring(1, 10) + "@getwhydah.com", "47" + Integer.toString(rand.nextInt(100000000)));
        return user;

    }
    

}
