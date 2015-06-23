package net.whydah.sso.user;

import org.junit.Test;
import org.slf4j.Logger;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by baardl on 22.06.15.
 */
public class UserRoleMapperTest {
    private static final Logger log = getLogger(UserRoleMapperTest.class);



    @Test
    public void testRolesFromXml() throws Exception {
        log.debug("Try to parse xml {}", userAggregateXML);
        List<UserRole> roles = UserRoleXPathHelper.getUserRoleFromUserAggregateXML(userAggregateXML);
        assertNotNull(roles);
        assertEquals(2,roles.size());

    }

    String userAggregateXML = "\n" +
            "<whydahuser>\n" +
            "    <identity>\n" +
            "        <username>admin</username>\n" +
            "        <cellPhone>+1555406789</cellPhone>\n" +
            "        <email>useradmin@getwhydah.com</email>\n" +
            "        <firstname>User</firstname>\n" +
            "        <lastname>Admin</lastname>\n" +
            "        <personRef>0</personRef>\n" +
            "        <UID>useradmin</UID>\n" +
            "    </identity>\n" +
            "    <applications>\n" +
            "        <application>\n" +
            "            <appId>19</appId>\n" +
            "            <applicationName>UserAdminWebApplication</applicationName>\n" +
            "            <orgName>Support</orgName>\n" +
            "            <roleName>WhydahUserAdmin</roleName>\n" +
            "            <roleValue>1</roleValue>\n" +
            "        </application>\n" +
            "        <application>\n" +
            "            <appId>19</appId>\n" +
            "            <applicationName>UserAdminWebApplication</applicationName>\n" +
            "            <orgName>Company</orgName>\n" +
            "            <roleName>WhydahUserAdmin</roleName>\n" +
            "            <roleValue>1</roleValue>\n" +
            "        </application>\n" +
            "    </applications>\n" +
            "</whydahuser>";
    String rolesXml = "<applications>\n" +
            "        <application>\n" +
            "            <appId>19</appId>\n" +
            "            <applicationName>UserAdminWebApplication</applicationName>\n" +
            "            <orgName>Support</orgName>\n" +
            "            <roleName>WhydahUserAdmin</roleName>\n" +
            "            <roleValue>1</roleValue>\n" +
            "        </application>\n" +
            "        <application>\n" +
            "            <appId>19</appId>\n" +
            "            <applicationName>UserAdminWebApplication</applicationName>\n" +
            "            <orgName>Company</orgName>\n" +
            "            <roleName>WhydahUserAdmin</roleName>\n" +
            "            <roleValue>1</roleValue>\n" +
            "        </application>\n" +
            "    </applications>";
}