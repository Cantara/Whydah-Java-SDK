package net.whydah.sso.user;

import net.whydah.sso.user.helpers.UserHelper;
import net.whydah.sso.user.helpers.UserRoleJsonPathHelper;
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

    String userTokenXML = """
            <usertoken xmlns:ns2="http://www.w3.org/1999/xhtml" id="a96a517f-cef3-4be7-92f5-f059b65e4071">
                <uid></uid>
                <timestamp></timestamp>
                <lifespan>3600000</lifespan>
                <issuer>/token/issuer/tokenverifier</issuer>
                <securitylevel>0</securitylevel>
                <username>test</username>
                <firstname>Olav</firstname>
                <lastname>Nordmann</lastname>
                <email></email>
                <personRef></personRef>
                <lastSeen></lastSeen>  <!-- Whydah 2.1 date and time of last registered user session -->
                <application ID="2349785543">
                    <applicationName>Whydah.net</applicationName>
                       <organizationName>Kunde 3</organizationName>
                          <role name="styremedlem" value=""/>
                          <role name="president" value=""/>
                       <organizationName>Kunde 4</organizationName>
                          <role name="styremedlem" value=""/>
                </application>
                <application ID="appa">
                    <applicationName>whydag.org</applicationName>
                    <organizationName>Kunde 1</organizationName>
                    <role name="styremedlem" value="Valla"/>
                </application>
            \s
                <ns2:link type="application/xml" href="/" rel="self"/>
                <hash type="MD5">8a37ef9624ed93db4873035b0de3d1ca</hash>
            </usertoken>""";

    String userAggregateXML = """
            
            <whydahuser>
                <identity>
                    <username>admin</username>
                    <cellPhone>+1555406789</cellPhone>
                    <email>useradmin@getwhydah.com</email>
                    <firstname>User</firstname>
                    <lastname>Admin</lastname>
                    <personRef>0</personRef>
                    <UID>useradmin</UID>
                </identity>
                <applications>
                    <application>
                        <appId>19</appId>
                        <applicationName>UserAdminWebApplication</applicationName>
                        <orgName>Support</orgName>
                        <roleName>WhydahUserAdmin</roleName>
                        <roleValue>1</roleValue>
                    </application>
                    <application>
                        <appId>19</appId>
                        <applicationName>UserAdminWebApplication</applicationName>
                        <orgName>Support</orgName>
                        <roleName>Manager</roleName>
                        <roleValue>true</roleValue>
                    </application>
                    <application>
                        <appId>19</appId>
                        <applicationName>UserAdminWebApplication</applicationName>
                        <orgName>Company</orgName>
                        <roleName>WhydahUserAdmin</roleName>
                        <roleValue>1</roleValue>
                    </application>
                </applications>
            </whydahuser>""";

    String userAggregateJson = """
            {
              "uid": "uid",
              "username": "usernameABC",
              "firstName": "firstName",
              "lastName": "lastName",
              "personRef": "personRef",
              "email": "email",
              "cellPhone": "12345678",
              "password": "password",
              "roles": [
                {
                  "applicationId": "applicationId",
                  "applicationName": "applicationName",
                  "organizationId": "organizationId",
                  "organizationName": "organizationName",
                  "applicationRoleName": "roleName",
                  "applicationRoleValue": "email"
                },
                {
                  "applicationId": "applicationId123",
                  "applicationName": "applicationName123",
                  "organizationId": "organizationId123",
                  "organizationName": "organizationName123",
                  "applicationRoleName": "roleName123",
                  "applicationRoleValue": "roleValue123"
                }
              ]
            }\
            """;

    String rolesXml = """
            <applications>
                    <application>
                        <appId>19</appId>
                        <applicationName>UserAdminWebApplication</applicationName>
                        <orgName>Support</orgName>
                        <roleName>WhydahUserAdmin</roleName>
                        <roleValue>1</roleValue>
                    </application>
                    <application>
                        <appId>19</appId>
                        <applicationName>UserAdminWebApplication</applicationName>
                        <orgName>Company</orgName>
                        <roleName>WhydahUserAdmin</roleName>
                        <roleValue>1</roleValue>
                    </application>
                </applications>""";


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
        assertTrue("19".equals(roles.getFirst().getApplicationId()));
        assertEquals("WhydahUserAdmin", roles.getFirst().getRoleName());
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
        UserApplicationRoleEntry roles[] = UserRoleJsonPathHelper.getUserRoleFromUserAggregateJson(userAggregateJson);
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
