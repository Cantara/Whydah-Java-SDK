package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.user.UserCredential;
import org.junit.Before;
import org.junit.Ignore;
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

    public static final String TEMPORARY_APPLICATION_ID = "201";//"11";
    public static final String TEMPORARY_APPLICATION_SECRET = "33779936R6Jr47D4Hj5R6p9qT";
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

    @Ignore
    @Test
    public void testLogonApplication() throws Exception{
        ApplicationCredential appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID,TEMPORARY_APPLICATION_SECRET);
        myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        assertTrue(myApplicationTokenID!=null && myApplicationTokenID.length()>5);

    }
}