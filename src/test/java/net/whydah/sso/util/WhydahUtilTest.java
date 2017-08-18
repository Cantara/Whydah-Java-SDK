package net.whydah.sso.util;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.types.UserCredential;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.net.URI;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by baardl on 18.06.15.
 */
public class WhydahUtilTest {
    private static final Logger log = getLogger(WhydahUtilTest.class);
    public static String TEMPORARY_APPLICATION_ID = "11";//"11";
    public static String TEMPORARY_APPLICATION_NAME = "MyApp";//"11";
    public static String TEMPORARY_APPLICATION_SECRET = "6r46g3q986Ep6By7B9J46m96D";
    public static String userName = "admin";
    public static String password = "whydahadmin";
    //    public static final String TEMPORARY_APPLICATION_ID = "99";//"11";
//    public static final String TEMPORARY_APPLICATION_SECRET = "33879936R6Jr47D4Hj5R6p9qT";
    private final String userAdminServiceUri = "http://localhost:9992/useradminservice";
    private final String userTokenServiceUri = "http://localhost:9998/tokenservice";
    private String myApplicationTokenID = null;
    private String myAppTokenXml = null;
    private URI tokenServiceUri = null;
    private UserCredential userCredential = null;
    private String adminUserTokenId = null;

    @Before
    public void setUp() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {
            URI tokenServiceUri = URI.create(userTokenServiceUri);
            ApplicationCredential appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
            myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
            myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            userCredential = new UserCredential(userName, password);
            log.debug("Logged in service {}", myApplicationTokenID);
            String adminUserTokenXml = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, UUID.randomUUID().toString()).execute();
            adminUserTokenId = UserXpathHelper.getUserTokenId(adminUserTokenXml);
            log.debug("Logged in admin {}", adminUserTokenId);
        }

    }

    @Test
    public void testLogOnApplicationAndUser() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {
            assertNotNull(adminUserTokenId);
        }

    }


    @Test
    public void testGetIpAddressesString() throws Exception {
        String ipadresses = WhydahUtil.getMyIPAddresssesString();
        assertNotNull(ipadresses);


    }



}