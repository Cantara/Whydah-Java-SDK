package net.whydah.sso.user;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by baardl on 19.06.15.
 */
public class UserXpathHelperTest {

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
        System.out.printf("timestamp:"+timestamp);
    }

    private static String userTokenXML = UserHelper.getDummyUserToken();

    private static String userIdentityXML = UserHelper.userDummyIdentityXML();

    private static String roleXml = "<appauth>            <id>b6767d13-4ca7-432c-8356-2b7c15cebc9a</id>\n" +
            "            <uid>_temp_username4Role_1434726891061</uid>\n" +
            "            <appId>201</appId>\n" +
            "            <applicationName></applicationName>\n" +
            "            <orgName>testOrg</orgName>\n" +
            "            <roleName>testRoleName</roleName>\n" +
            "            <roleValue>true</roleValue>\n" +
            "        </appauth>";
}