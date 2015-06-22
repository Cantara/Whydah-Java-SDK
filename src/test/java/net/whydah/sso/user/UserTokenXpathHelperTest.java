package net.whydah.sso.user;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by totto on 22.06.15.
 */
public class UserTokenXpathHelperTest {

    String userTokenXML = "\n" +
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<usertoken xmlns:ns2=\"http://www.w3.org/1999/xhtml\" id=\"a96a517f-cef3-4be7-92f5-f059b65e4071\">\n" +
            "    <uid></uid>\n" +
            "    <timestamp></timestamp>\n" +
            "    <lifespan>3600000</lifespan>\n" +
            "    <issuer>/token/issuer/tokenverifier</issuer>\n" +
            "    <securitylevel>0</securitylevel>\n" +
            "    <username></username>\n" +
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

    String userAggregateXML="\n" +
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

    String userAggregateJson="{\n" +
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


    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetUserRoleFromUserToken() throws Exception {
        UserRole roles[] = UserRoleXPathHelper.getUserRoleFromUserToken(userTokenXML);

    }
    @Test
    public void testGetUserRoleFromUserAggregateXML() throws Exception {
        UserRole roles[] = UserRoleXPathHelper.getUserRoleFromUserAggregateXML(userAggregateXML);

    }
    @Test
    public void testGetUserRoleFromUserAggregateJSON() throws Exception {
        UserRole roles[] = UserRoleXPathHelper.getUserRoleFromUserAggregateJSON(userAggregateJson);

    }

}
