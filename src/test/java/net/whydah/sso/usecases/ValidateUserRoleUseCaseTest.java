package net.whydah.sso.usecases;


import net.whydah.sso.WhydahApplicationSession;
import net.whydah.sso.WhydahTemporaryBliUtil;
import net.whydah.sso.WhydahUtil;
import net.whydah.sso.user.UserCredential;
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

    public static final String TEMPORARY_APPLICATION_ID = "99";//"11";
    public static final String TEMPORARY_APPLICATION_SECRET = "33879936R6Jr47D4Hj5R6p9qT";
    public static final String userName = "admin";
    public static final String password = "admin";

//    private final WebTarget userAdminService;
    private final String userAdminServiceUri = "http://localhost:9992/useradminservice";
    private final String userTokenServiceUri = "http://localhost:9998/tokenservice";


    @Before
    public void setUp() throws Exception {

    }

    @Ignore
    @Test
    public void test1_logonApplication() throws Exception{
        WhydahApplicationSession applicationSession = new WhydahApplicationSession(userTokenServiceUri, TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_SECRET);
        assertNotNull(applicationSession);
    }

    @Ignore
    @Test
    public void test2_logonUser() throws Exception{
        UserCredential userCredential = new UserCredential(userName,password);
        WhydahApplicationSession applicationSession = new WhydahApplicationSession(userTokenServiceUri, TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_SECRET);
        String userTokenXml = WhydahUtil.logOnUser(applicationSession, userCredential);
        assertNotNull(userTokenXml);
    }

    @Ignore
    @Test
    public void bli_test2_logonUser() throws Exception {
        WhydahApplicationSession applicationSession = new WhydahApplicationSession(userTokenServiceUri, TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_SECRET);
        String appTokenId = applicationSession.getActiveApplicationTokenId();
        String appTokenXml = applicationSession.getActiveApplicationToken();
        String userTokenXml = WhydahTemporaryBliUtil.logOnUser(userTokenServiceUri,appTokenId, appTokenXml, userName, password);
        assertNotNull(userTokenXml);
        assertTrue(userTokenXml.contains(userName));
    }

    @Ignore
    @Test
    public void bli_test3_validateRole() throws Exception {
        WhydahApplicationSession applicationSession = new WhydahApplicationSession(userTokenServiceUri, TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_SECRET);
        String appTokenId = applicationSession.getActiveApplicationTokenId();
        String appTokenXml = applicationSession.getActiveApplicationToken();
        String userTokenXml = WhydahTemporaryBliUtil.logOnUser(userTokenServiceUri,appTokenId, appTokenXml, userName, password);
        assertNotNull(userTokenXml);
        assertTrue(userTokenXml.contains(userName));
    }
}
