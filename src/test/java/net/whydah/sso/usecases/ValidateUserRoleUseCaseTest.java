package net.whydah.sso.usecases;


import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.session.WhydahUserSession;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SystemTestBaseConfig;
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
    public static SystemTestBaseConfig config;


    private final String roleName = "WhydahUserAdmin";
    private static WhydahApplicationSession applicationSession;

    @Before
    public void setUp() throws Exception {
        config = new SystemTestBaseConfig();
        if (config.isSystemTestEnabled()) {
            applicationSession = WhydahApplicationSession.getInstance(config.tokenServiceUri.toString(), config.userAdminServiceUri.toString(), config.appCredential);
        }
    }

    @Test
    public void test1_logonApplication() throws Exception {
        if (config.isSystemTestEnabled()) {
            log.debug("Active ApplicationId:{}", applicationSession.getActiveApplicationTokenId());
            assertTrue(applicationSession.checkActiveSession());
        }
    }

    @Test
    public void test2_logonUser() throws Exception {
        if (config.isSystemTestEnabled()) {
            UserCredential userCredential = new UserCredential(config.userName, config.password);
            assertTrue(applicationSession.checkActiveSession());
            String userTokenXml = WhydahUtil.logOnUser(applicationSession, userCredential);
            // re-try to check if that is all which is needed after a whydah full reboot scenario
            if (userTokenXml == null) {
                Thread.sleep(2000);
                userTokenXml = WhydahUtil.logOnUser(applicationSession, userCredential);
            }
            assertNotNull(userTokenXml);
            assertTrue(userTokenXml.contains(config.userEmail));
        }
    }

    @Ignore
    @Test   // NB takes 35 minutes to complete......
    public void test2_logonUserSession() throws Exception {
        if (config.isSystemTestEnabled()) {
            UserCredential userCredential = new UserCredential(config.userName, config.password);
            assertTrue(applicationSession.checkActiveSession());
            WhydahUserSession userSession = new WhydahUserSession(applicationSession, userCredential);
            assertTrue(userSession.hasActiveSession());
            assertNotNull(userSession.getActiveUserToken());
            assertTrue(userSession.getActiveUserToken().contains(config.userEmail));
            Thread.sleep(1000 * 1000);
            assertTrue(applicationSession.checkActiveSession());
            WhydahUserSession userSession2 = new WhydahUserSession(applicationSession, userCredential);
            assertNotNull(userSession2.getActiveUserToken());
            assertTrue(userSession2.getActiveUserToken().contains(config.userEmail));
            assertTrue(userSession.getActiveUserToken().contains(config.userEmail));
            userSession.getActiveUserTokenId();
            Thread.sleep(1000 * 1000);
        }
    }

    @Test
    public void bli_test2_logonUser() throws Exception {
        if (config.isSystemTestEnabled()) {
            assertTrue(applicationSession.checkActiveSession());
            String appTokenId = applicationSession.getActiveApplicationTokenId();
            log.trace("appTokenId {}", appTokenId);
            UserCredential userCredential = new UserCredential(config.userName, config.password);
            String userTokenXml = WhydahUtil.logOnUser(applicationSession, userCredential);
            assertNotNull(userTokenXml);
            log.trace("userTokenId {}", UserXpathHelper.getUserTokenId(userTokenXml));
            assertTrue(userTokenXml.contains(config.userName));
        }
    }

    @Test
    public void bli_test3_validateRole() throws Exception {
        if (config.isSystemTestEnabled()) {
            assertTrue(applicationSession.checkActiveSession());
            String appTokenId = applicationSession.getActiveApplicationTokenId();
            log.trace("appTokenId {}", appTokenId);
            UserCredential userCredential = new UserCredential(config.userName, config.password);
            String userTokenXml = WhydahUtil.logOnUser(applicationSession, userCredential);
            assertNotNull(userTokenXml);
            log.debug("userTokenXml {}", userTokenXml);
            assertTrue(userTokenXml.contains(config.userName));
            String userId = UserXpathHelper.getUserIdFromUserTokenXml(userTokenXml);
            log.trace("userId {}", userId);
            log.trace("userTokenId {}", UserXpathHelper.getUserTokenId(userTokenXml));

            String userTokenXmlAfter = WhydahUtil.logOnUser(applicationSession, userCredential);
//            assertNotNull(createdRoles);
//            assertTrue(createdRoles.size() >=1);
            assertTrue(UserXpathHelper.hasRoleFromUserToken(userTokenXmlAfter, "2219", roleName));

           // UserRole userRole = createdRoles.get(1);
          //  assertEquals("TestOrg",userRole.getOrgName());
          //  assertEquals("TestRolename",userRole.getRoleName());
        }
    }
}
