package net.whydah.sso.usecases;


import net.whydah.sso.WhydahApplicationSession;
import net.whydah.sso.WhydahTemporaryBliUtil;
import net.whydah.sso.WhydahUserSession;
import net.whydah.sso.WhydahUtil;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserRole;
import net.whydah.sso.user.UserXpathHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.List;

import static org.junit.Assert.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by baardl on 26.06.15.
 */
public class ValidateUserRoleUseCaseTest {
    private static final Logger log = getLogger(ValidateUserRoleUseCaseTest.class);

    public static String TEMPORARY_APPLICATION_ID = "99";//"11";
    public static String TEMPORARY_APPLICATION_SECRET = "33879936R6Jr47D4Hj5R6p9qT";
    public static String userName = "admin";
    public static String password = "admin";

    private String userAdminServiceUri = "http://localhost:9992/useradminservice";
    private String userTokenServiceUri = "http://localhost:9998/tokenservice";
    private static boolean integrationMode = false;


    @Before
    public void setUp() throws Exception {
        if (integrationMode) {
            userAdminServiceUri = "https://whydahdev.altrancloud.com/useradminservice";
            userTokenServiceUri = "https://whydahdev.altrancloud.com/tokenservice";

        }
    }

    @Ignore
    @Test
    public void test1_logonApplication() throws Exception {
        WhydahApplicationSession applicationSession = new WhydahApplicationSession(userTokenServiceUri, TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_SECRET);
        System.out.println("Active ApplicationId:" + applicationSession.getActiveApplicationTokenId());
        assertTrue(applicationSession.hasActiveSession());
    }

    @Ignore
    @Test
    public void test2_logonUser() throws Exception {
        UserCredential userCredential = new UserCredential(userName, password);
        WhydahApplicationSession applicationSession = new WhydahApplicationSession(userTokenServiceUri, TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_SECRET);
        assertTrue(applicationSession.hasActiveSession());
        String userTokenXml = WhydahUtil.logOnUser(applicationSession, userCredential);
        assertNotNull(userTokenXml);
        assertTrue(userTokenXml.contains("useradmin@altran.com"));
    }

    @Ignore
    @Test
    public void test2_logonUserSession() throws Exception {
        UserCredential userCredential = new UserCredential(userName, password);
        WhydahApplicationSession applicationSession = new WhydahApplicationSession(userTokenServiceUri, TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_SECRET);
        assertTrue(applicationSession.hasActiveSession());
        WhydahUserSession userSession = new WhydahUserSession(applicationSession,userCredential);
        assertTrue(userSession.hasActiveSession());
        String userTokenXml = userSession.getActiveUserToken();
        assertNotNull(userTokenXml);
        assertTrue(userTokenXml.contains("useradmin@altran.com"));
    }

    @Ignore
    @Test
    public void bli_test2_logonUser() throws Exception {
        WhydahApplicationSession applicationSession = new WhydahApplicationSession(userTokenServiceUri, TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_SECRET);
        assertTrue(applicationSession.hasActiveSession());
        String appTokenId = applicationSession.getActiveApplicationTokenId();
        log.trace("appTokenId {}", appTokenId);
        String appTokenXml = applicationSession.getActiveApplicationToken();
        String userTokenXml = WhydahTemporaryBliUtil.logOnUser(userTokenServiceUri, appTokenId, appTokenXml, userName, password);
        assertNotNull(userTokenXml);
        log.trace("userTokenId {}", UserXpathHelper.getUserTokenId(userTokenXml));
        assertTrue(userTokenXml.contains(userName));
    }

    @Ignore
    @Test
    public void bli_test3_validateRole() throws Exception {
        WhydahApplicationSession applicationSession = new WhydahApplicationSession(userTokenServiceUri, TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_SECRET);
        assertTrue(applicationSession.hasActiveSession());
        String appTokenId = applicationSession.getActiveApplicationTokenId();
        log.trace("appTokenId {}", appTokenId);
        String appTokenXml = applicationSession.getActiveApplicationToken();
        String userTokenXml = WhydahTemporaryBliUtil.logOnUser(userTokenServiceUri, appTokenId, appTokenXml, userName, password);
        assertNotNull(userTokenXml);
        log.debug("userTokenXml {}", userTokenXml);
        assertTrue(userTokenXml.contains(userName));
        String adminUserTokenId = UserXpathHelper.getUserTokenId(userTokenXml);
        String userId = UserXpathHelper.getUserIdFromUserTokenXml(userTokenXml);
        log.trace("userId {}", userId);
        log.trace("userTokenId {}", UserXpathHelper.getUserTokenId(userTokenXml));
        List<UserRole> createdRoles = WhydahUtil.listUserRoles(userAdminServiceUri, appTokenId, adminUserTokenId, TEMPORARY_APPLICATION_ID, userId);
        assertNotNull(createdRoles);
        assertEquals(createdRoles.size(), 1);
        UserRole userRole = createdRoles.get(0);
        assertEquals(userRole.getOrgName(), "Whydah");
        assertEquals(userRole.getRoleName(), "WhydahUserAdmin");
    }
}
