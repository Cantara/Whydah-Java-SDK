package net.whydah.sso.commands.adminapi;

import net.whydah.sso.application.*;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.appauth.CommandLogonApplicationWithStubbedFallback;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredentialWithStubbedFallback;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserXpathHelper;
import net.whydah.sso.util.SystemTestUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * Created by totto on 24.06.15.
 */
public class CommandListApplicationsTest {

    public static String userName = "admin";
    public static String password = "whydahadmin";
    private static URI tokenServiceUri;
    private static ApplicationCredential appCredential;
    private static UserCredential userCredential;
    private static boolean systemTest = false;
    private static URI userAdminServiceUri;

    @BeforeClass
    public static void setup() throws Exception {
        appCredential = new ApplicationCredential("15","33779936R6Jr47D4Hj5R6p9qT");
        tokenServiceUri = UriBuilder.fromUri("https://no_host").build();
        userCredential = new UserCredential(userName, password);

        userAdminServiceUri = UriBuilder.fromUri("https://no_host").build();

        if (systemTest) {
            tokenServiceUri = UriBuilder.fromUri("https://whydahdev.altrancloud.com/tokenservice/").build();
            userAdminServiceUri = UriBuilder.fromUri("https://whydahdev.altrancloud.com/useradminservice/").build();
        }
    }


    @Test
    public void testListApplicationsCommandWithFallback() throws Exception {

        String myAppTokenXml = new CommandLogonApplicationWithStubbedFallback(tokenServiceUri, appCredential).execute();
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
        String userticket = UUID.randomUUID().toString();
        String userToken = new CommandLogonUserByUserCredentialWithStubbedFallback(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
        String userTokenId = UserXpathHelper.getUserTokenId(userToken);
        assertTrue(userTokenId!=null && userTokenId.length()>5);

        String applicationsJsonl = new CommandListApplicationsWithStubbedFallback(userAdminServiceUri, myApplicationTokenID,userTokenId,"").execute();
        System.out.println("applicationsJson=" + applicationsJsonl);
        assertTrue(applicationsJsonl.equalsIgnoreCase(ApplicationHelper.getDummyAppllicationListJson()));

    }

    @Test
    public void testListApplicationsCommand() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {
            tokenServiceUri = UriBuilder.fromUri("http://localhost:9998/tokenservice/").build();
            userAdminServiceUri =  UriBuilder.fromUri("http://localhost:9992/useradminservice").build();

            String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            assertTrue(userTokenId != null && userTokenId.length() > 5);

            String applicationsJson = new CommandListApplications(userAdminServiceUri, myApplicationTokenID, userTokenId, "").execute();
            System.out.println("applicationsJson=" + applicationsJson);
            assertTrue(applicationsJson.length() > 100);
            List<Application> applications = ApplicationSerializer.fromJsonList(applicationsJson);
            assertTrue(applications.size() > 6);

        }
    }

    }
