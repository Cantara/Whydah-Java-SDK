package net.whydah.sso.user;

import net.whydah.sso.user.helpers.UserHelper;
import net.whydah.sso.user.helpers.UserRoleXpathHelper;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.List;

import static org.junit.Assert.*;
import static org.slf4j.LoggerFactory.getLogger;

public class UserTokenXpathHelperTest {
    private static final Logger log = getLogger(UserTokenXpathHelperTest.class);

    String userTokenXML = "<usertoken xmlns:ns2=\"http://www.w3.org/1999/xhtml\" id=\"a96a517f-cef3-4be7-92f5-f059b65e4071\">\n" +
            "    <uid></uid>\n" +
            "    <timestamp></timestamp>\n" +
            "    <lifespan>3600000</lifespan>\n" +
            "    <issuer>/token/issuer/tokenverifier</issuer>\n" +
            "    <securitylevel>0</securitylevel>\n" +
            "    <username>test</username>\n" +
            "    <firstname>Olav</firstname>\n" +
            "    <lastname>Nordmann</lastname>\n" +
            "    <email></email>\n" +
            "    <personRef></personRef>\n" +
            "    <lastSeen></lastSeen>  <!-- Whydah 2.1 date and time of last registered user session -->\n" +
            "    <application ID=\"2349785543\">\n" +
            "        <applicationName>Whydah.net</applicationName>\n" +
            "           <organizationName>Kunde 3</organizationName>\n" +
            "              <role name=\"styremedlem\" value=\"\"/>\n" +
            "              <role name=\"president\" value=\"\"/>\n" +
            "           <organizationName>Kunde 4</organizationName>\n" +
            "              <role name=\"styremedlem\" value=\"\"/>\n" +
            "    </application>\n" +
            "    <application ID=\"appa\">\n" +
            "        <applicationName>whydag.org</applicationName>\n" +
            "        <organizationName>Kunde 1</organizationName>\n" +
            "        <role name=\"styremedlem\" value=\"Valla\"/>\n" +
            "    </application>\n" +
            " \n" +
            "    <ns2:link type=\"application/xml\" href=\"/\" rel=\"self\"/>\n" +
            "    <hash type=\"MD5\">8a37ef9624ed93db4873035b0de3d1ca</hash>\n" +
            "</usertoken>";

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
            "            <orgName>Support</orgName>\n" +
            "            <roleName>Manager</roleName>\n" +
            "            <roleValue>true</roleValue>\n" +
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

    String userAggregateJson = "{\n" +
            "  \"uid\": \"uid\",\n" +
            "  \"username\": \"usernameABC\",\n" +
            "  \"firstName\": \"firstName\",\n" +
            "  \"lastName\": \"lastName\",\n" +
            "  \"personRef\": \"personRef\",\n" +
            "  \"email\": \"email\",\n" +
            "  \"cellPhone\": \"12345678\",\n" +
            "  \"password\": \"password\",\n" +
            "  \"roles\": [\n" +
            "    {\n" +
            "      \"applicationId\": \"applicationId\",\n" +
            "      \"applicationName\": \"applicationName\",\n" +
            "      \"organizationId\": \"organizationId\",\n" +
            "      \"organizationName\": \"organizationName\",\n" +
            "      \"applicationRoleName\": \"roleName\",\n" +
            "      \"applicationRoleValue\": \"email\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"applicationId\": \"applicationId123\",\n" +
            "      \"applicationName\": \"applicationName123\",\n" +
            "      \"organizationId\": \"organizationId123\",\n" +
            "      \"organizationName\": \"organizationName123\",\n" +
            "      \"applicationRoleName\": \"roleName123\",\n" +
            "      \"applicationRoleValue\": \"roleValue123\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

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


    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetUserRoleFromUserToken() throws Exception {
        UserApplicationRoleEntry roles[] = UserRoleXpathHelper.getUserRoleFromUserTokenXml(userTokenXML);
        assertTrue("2349785543".equals(roles[0].getApplicationId()));
        assertTrue("appa".equals(roles[2].getApplicationId()));

    }

    @Test
    public void testGetUserRoleFromUserAggregateXML() throws Exception {
        List<UserApplicationRoleEntry> roles = UserRoleXpathHelper.getUserRoleFromUserAggregateXml(userAggregateXML);
        assertNotNull(roles);
        assertEquals(3, roles.size());
        assertTrue("19".equals(roles.get(0).getApplicationId()));
        assertEquals("WhydahUserAdmin", roles.get(0).getRoleName());
        assertEquals("Manager", roles.get(1).getRoleName());
        assertEquals("WhydahUserAdmin", roles.get(2).getRoleName());
        assertTrue("Company".equals(roles.get(2).getOrgName()));

    }

    @Test
    public void testHasRole(){
        String userToken = UserHelper.getDummyUserToken();
        assertTrue(UserXpathHelper.hasRoleFromUserToken(userToken, "2349785543", "president"));
    }

    @Test
    public void testHasNotRole(){
        String userToken = UserHelper.getDummyUserToken();
        assertFalse(UserXpathHelper.hasRoleFromUserToken(userToken, "2349785543", "pprreessiiddeenntt"));
    }

    @Test
    public void testGetUserRoleFromUserAggregateJSON() throws Exception {
        UserApplicationRoleEntry roles[] = UserRoleXpathHelper.getUserRoleFromUserAggregateJson(userAggregateJson);
        assertTrue("applicationId".equals(roles[0].getApplicationId()));
        assertTrue("applicationId123".equals(roles[1].getApplicationId()));

    }

    @Test
    public void testRolesFromXml() throws Exception {
        log.debug("Try to parse xml {}", userAggregateXML);
        List<UserApplicationRoleEntry> roles = UserRoleXpathHelper.getUserRoleFromUserAggregateXml(userAggregateXML);
        assertNotNull(roles);
        assertEquals(3, roles.size());

    }

    @Test
    public void testGetUserRoleFromUserAggregateXml() throws Exception {
        log.debug("Try to parse xml {}", rolesXml);
        List<UserApplicationRoleEntry> roles = UserRoleXpathHelper.getUserRoleFromUserAggregateXml(userAggregateXML);
        assertNotNull(roles);
        assertEquals(3, roles.size());
    }


}
