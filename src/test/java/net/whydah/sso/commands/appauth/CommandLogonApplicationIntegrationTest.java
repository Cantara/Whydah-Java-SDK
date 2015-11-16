package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SystemTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by baardl on 24.06.15.
 */
public class CommandLogonApplicationIntegrationTest {
    private static final Logger log = getLogger(CommandLogonApplicationIntegrationTest.class);

    //    public static final String TEMPORARY_APPLICATION_ID = "201";//"11";
//    public static final String TEMPORARY_APPLICATION_SECRET = "33779936R6Jr47D4Hj5R6p9qT";
    public static String TEMPORARY_APPLICATION_ID = "11";//"11";
    public static String TEMPORARY_APPLICATION_SECRET = "6r46g3q986Ep6By7B9J46m96D";
    public static String userName = "admin";
    public static String password = "whydahadmin";

    private final String userAdminServiceUri = "http://localhost:9992/useradminservice";
    private final String userTokenServiceUri = "http://localhost:9998/tokenservice";
    private String myApplicationTokenID = null;
    private String myAppTokenXml = null;
    private URI tokenServiceUri = null;
    private UserCredential userCredential = null;
    private String adminUserTokenId = null;
    private String adminUserId = null;


    @Before
    public void setUp() throws Exception {
        tokenServiceUri = UriBuilder.fromUri(userTokenServiceUri).build();


    }

    @Test
    public void testLogonApplication() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {
            ApplicationCredential appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_SECRET);
            myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
            myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);

        }
    }
}