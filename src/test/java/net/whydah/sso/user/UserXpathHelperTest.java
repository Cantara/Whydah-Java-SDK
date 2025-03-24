package net.whydah.sso.user;

import net.whydah.sso.user.helpers.UserHelper;
import net.whydah.sso.user.helpers.UserRoleXpathHelper;
import net.whydah.sso.user.helpers.UserXpathHelper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by baardl on 19.06.15.
 */
public class UserXpathHelperTest {

    private static String userTokenXML = UserHelper.getDummyUserToken();
    private static String userIdentityXML = UserHelper.userDummyIdentityXML();
    private static String roleXml = """
            <application>            <id>b6767d13-4ca7-432c-8356-2b7c15cebc9a</id>
                        <uid>_temp_username4Role_1434726891061</uid>
                        <appId>201</appId>
                        <applicationName></applicationName>
                        <orgName>testOrg</orgName>
                        <roleName>testRoleName</roleName>
                        <roleValue>true</roleValue>
                    </application>""";
    private static String userAdminTokenXml = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <usertoken xmlns:ns2="http://www.w3.org/1999/xhtml" id="9739d138-6a42-4fbd-8281-c7775e77c110">
                <uid>useradmin</uid>
                <timestamp>1435317025896</timestamp>
                <lifespan>1209600000</lifespan>
                <issuer>http://localhost:9998/tokenservice/user/ac627ab1ccbdb62ec96e702f07f6425b/validate_usertokenid/9739d138-6a42-4fbd-8281-c7775e77c110</issuer>
                <securitylevel>1</securitylevel>
                <DEFCON>DEFCON5</DEFCON>
                <username>admin</username>
                <firstname>User</firstname>
                <lastname>Admin</lastname>
                <email>useradmin@altran.com</email>
                <personRef>0</personRef>
            
                <ns2:link type="application/xml" href="http://localhost:9998/tokenservice/user/ac627ab1ccbdb62ec96e702f07f6425b/validate_usertokenid/9739d138-6a42-4fbd-8281-c7775e77c110" rel="self"/>
                <hash type="MD5">59afe7053257ea6559e1819047ff2223</hash>
            </usertoken>
            
            """;
    private static String userWithRolesXML = UserHelper.getDummyUserToken();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetUserName() throws Exception {
        String userName = UserXpathHelper.getUserNameFromUserIdentityXml(UserHelper.userDummyIdentityXML());
        assertEquals("test_name", userName);

    }

    @Test
    public void testFindOrgName() throws Exception {
        String orgName = UserRoleXpathHelper.getOrgNameFromRoleXml(roleXml);
        assertEquals("testOrg", orgName);
    }

    @Test
    public void testExpiresFromUserToken() throws Exception {
        Long timestamp= UserXpathHelper.getTimestampFromUserTokenXml(UserHelper.getDummyUserToken());
        timestamp=timestamp+UserXpathHelper.getLifespanFromUserTokenXml(UserHelper.getDummyUserToken());
        System.out.printf("timestamp:" + timestamp);
    }

    @Test
    public void testGetUserNameFromUserToken() throws Exception {
        String userName = UserXpathHelper.getUserNameFromUserTokenXml(userAdminTokenXml);
        assertEquals("admin", userName);

    }

    @Test
    public void testFindUserIdFromUserTokenXml() throws Exception {
        String userAdminId = UserXpathHelper.getUserIdFromUserTokenXml(userAdminTokenXml);
        assertEquals("useradmin", userAdminId);
    }

    @Test
    public void testFindRoleNamesUserTokenXml() throws Exception {
        String hasNullRoleNames = UserXpathHelper.getRoleNamesFromUserToken(userAdminTokenXml);
        assertTrue(hasNullRoleNames == null || hasNullRoleNames.length() == 0);
        String hasRoleNames = UserXpathHelper.getRoleNamesFromUserToken(userWithRolesXML);
        assertTrue(hasRoleNames.length() > 1);
    }
}