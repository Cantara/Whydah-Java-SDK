package net.whydah.sso.usecases;


import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.session.WhydahUserSession;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SystemTestUtil;
import net.whydah.sso.util.WhydahUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by baardl on 26.06.15.
 */
public class ValidateUserRoleUseCaseTest {
    private static final Logger log = getLogger(ValidateUserRoleUseCaseTest.class);

    //public static String TEMPORARY_APPLICATION_ID = "99";//"11";
    //public static String TEMPORARY_APPLICATION_SECRET = "33879936R6Jr47D4Hj5R6p9qT";
    public static String TEMPORARY_APPLICATION_ID = "11";//"11";
    public static String TEMPORARY_APPLICATION_NAME = "MyApp";//"11";
    public static String TEMPORARY_APPLICATION_SECRET = "6r46g3q986Ep6By7B9J46m96D";
    public static String userName = "admin";
    public static String password = "whydahadmin";
    private static boolean integrationMode = false;
    private final String roleName = "WhydahUserAdmin";
    private String userAdminServiceUri = "http://localhost:9992/useradminservice";
    private String userTokenServiceUri = "http://localhost:9998/tokenservice";

    @Before
    public void setUp() throws Exception {
        if (integrationMode) {
            userAdminServiceUri = "https://whydahdev.altrancloud.com/useradminservice";
            userTokenServiceUri = "https://whydahdev.altrancloud.com/tokenservice";

        }
    }

    @Test
    public void test1_logonApplication() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {
            WhydahApplicationSession applicationSession = WhydahApplicationSession.getInstance(userTokenServiceUri, TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
            System.out.println("Active ApplicationId:" + applicationSession.getActiveApplicationTokenId());
            assertTrue(applicationSession.checkActiveSession());
        }
    }

    @Test
    public void test2_logonUser() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {

            UserCredential userCredential = new UserCredential(userName, password);
            WhydahApplicationSession applicationSession = WhydahApplicationSession.getInstance(userTokenServiceUri, TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
            assertTrue(applicationSession.checkActiveSession());
            String userTokenXml = WhydahUtil.logOnUser(applicationSession, userCredential);
            assertNotNull(userTokenXml);
            assertTrue(userTokenXml.contains("useradmin@whydah.net"));
        }
    }

    @Ignore
    @Test   // NB takes 35 minutes to complete......
    public void test2_logonUserSession() throws Exception {
        UserCredential userCredential = new UserCredential(userName, password);
        WhydahApplicationSession applicationSession = WhydahApplicationSession.getInstance(userTokenServiceUri, TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
        assertTrue(applicationSession.checkActiveSession());
        WhydahUserSession userSession = new WhydahUserSession(applicationSession,userCredential);
        assertTrue(userSession.hasActiveSession());
        assertNotNull(userSession.getActiveUserToken());
        assertTrue(userSession.getActiveUserToken().contains("useradmin@whydah.net"));
        Thread.sleep(1000 * 1000);
        assertTrue(applicationSession.checkActiveSession());
        WhydahUserSession userSession2 = new WhydahUserSession(applicationSession,userCredential);
        assertNotNull(userSession2.getActiveUserToken());
        assertTrue(userSession2.getActiveUserToken().contains("useradmin@whydah.net"));
        assertTrue(userSession.getActiveUserToken().contains("useradmin@whydah.net"));
        userSession.getActiveUserTokenId();
        Thread.sleep(1000 * 1000);
    }

    @Test
    @Ignore
    public void bli_test2_logonUser() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {

            WhydahApplicationSession applicationSession = WhydahApplicationSession.getInstance(userTokenServiceUri, TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
            assertTrue(applicationSession.checkActiveSession());
            String appTokenId = applicationSession.getActiveApplicationTokenId();
            log.trace("appTokenId {}", appTokenId);
            String appTokenXml = applicationSession.getActiveApplicationTokenXML();
            String userTokenXml = null;// WhydahTemporaryBliUtil.logOnUser(userTokenServiceUri, appTokenId, appTokenXml, userName, password);
            assertNotNull(userTokenXml);
            log.trace("userTokenId {}", UserXpathHelper.getUserTokenId(userTokenXml));
            assertTrue(userTokenXml.contains(userName));
        }
    }

    @Test
    @Ignore
    public void bli_test3_validateRole() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {

            WhydahApplicationSession applicationSession = WhydahApplicationSession.getInstance(userTokenServiceUri, TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
            assertTrue(applicationSession.checkActiveSession());
            String appTokenId = applicationSession.getActiveApplicationTokenId();
            log.trace("appTokenId {}", appTokenId);
            String appTokenXml = applicationSession.getActiveApplicationTokenXML();
            String userTokenXml = null;//  WhydahTemporaryBliUtil.logOnUser(userTokenServiceUri, appTokenId, appTokenXml, userName, password);
            assertNotNull(userTokenXml);
            log.debug("userTokenXml {}", userTokenXml);
            assertTrue(userTokenXml.contains(userName));
            String adminUserTokenId = UserXpathHelper.getUserTokenId(userTokenXml);
            String userId = UserXpathHelper.getUserIdFromUserTokenXml(userTokenXml);
            log.trace("userId {}", userId);
            log.trace("userTokenId {}", UserXpathHelper.getUserTokenId(userTokenXml));
//            List<UserRole> createdRoles = WhydahUtil.listUserRoles(userAdminServiceUri, appTokenId, adminUserTokenId, TEMPORARY_APPLICATION_ID, userId);
            String userTokenXmlAfter = null; //WhydahTemporaryBliUtil.logOnUser(userTokenServiceUri, appTokenId, appTokenXml, userName, password);
//            assertNotNull(createdRoles);
//            assertTrue(createdRoles.size() >=1);
            assertTrue(UserXpathHelper.hasRoleFromUserToken(userTokenXmlAfter, "19", roleName));

           // UserRole userRole = createdRoles.get(1);
          //  assertEquals("TestOrg",userRole.getOrgName());
          //  assertEquals("TestRolename",userRole.getRoleName());
        }
    }
}
