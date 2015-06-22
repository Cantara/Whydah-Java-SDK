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
        log.debug("Try to parse xml {}", rolesXml);
        List<UserRole> roles = UserRoleMapper.rolesFromXml(rolesXml);
        assertNotNull(roles);
        assertEquals(2,roles.size());

    }

    private String rolesXml = "<roles><application>\n" +
            "    <id>1</id>\n" +
            "    <uid>1b9f8738-e13b-47f2-895a-915a158f6b75</uid>\n" +
            "    <appId>201</appId>\n" +
            "    <applicationName>DomainConfig</applicationName>\n" +
            "    <orgName>testOrg</orgName>\n" +
            "    <roleName>testRoleName</roleName>\n" +
            "    <roleValue>true</roleValue>\n" +
            "</application>\n" +
            "<application>\n" +
            "    <id>2</id>\n" +
            "    <uid>1b9f8738-e13b-47f2-895a-915a158f6b75</uid>\n" +
            "    <appId>201</appId>\n" +
            "    <applicationName>DomainConfig</applicationName>\n" +
            "    <orgName>testOrg</orgName>\n" +
            "    <roleName>testRoleName</roleName>\n" +
            "    <roleValue>true</roleValue>\n" +
            "</application></roles>";
}