package net.whydah.sso.user;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by baardl on 19.06.15.
 */
public class UserXpathHelperTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetUserName() throws Exception {
        String userName = UserXpathHelper.getUserName(userTokenXML);
        assertEquals("test_name", userName);

    }

    @Test
    public void testFindValue() throws Exception {
        String expression = "/whydahuser/identity/username";
        String userId = UserXpathHelper.findValue(userTokenXML, expression);
        assertEquals("test_name", userId);


    }

    @Test
    public void testFindOrgName() throws Exception {
        String orgName = UserXpathHelper.getOrgName(roleXml);
        assertEquals("testOrg", orgName);
    }

    private static String userTokenXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<whydahuser>\n" +
            "    <identity>\n" +
            "        <username>test_name</username>\n" +
            "        <cellPhone>+4712345678</cellPhone>\n" +
            "        <email>_temp_username4Role_1434725294369@example.com</email>\n" +
            "        <firstname>first</firstname>\n" +
            "        <lastname>last</lastname>\n" +
            "        <personRef>ref</personRef>\n" +
            "        <UID>e2583c57-521a-4840-a3cc-e9de8e4a2c61</UID>\n" +
            "    </identity>\n" +
            "    <applications>\n" +
            "    </applications>\n" +
            "</whydahuser>";

    private static String roleXml = "<application>            <id>b6767d13-4ca7-432c-8356-2b7c15cebc9a</id>\n" +
            "            <uid>_temp_username4Role_1434726891061</uid>\n" +
            "            <appId>201</appId>\n" +
            "            <applicationName></applicationName>\n" +
            "            <orgName>testOrg</orgName>\n" +
            "            <roleName>testRoleName</roleName>\n" +
            "            <roleValue>true</roleValue>\n" +
            "        </application>";
}