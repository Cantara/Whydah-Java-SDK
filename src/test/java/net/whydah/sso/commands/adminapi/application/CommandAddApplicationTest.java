package net.whydah.sso.commands.adminapi.application;


import net.whydah.sso.application.helpers.ApplicationHelper;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.mappers.ApplicationMapper;
import net.whydah.sso.application.types.Application;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SSLTool;
import net.whydah.sso.util.SystemTestUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class CommandAddApplicationTest {

    public static String TEMPORARY_APPLICATION_ID = "11";//"11";
    public static String TEMPORARY_APPLICATION_NAME = "Funny APp";//"11";
    public static String TEMPORARY_APPLICATION_SECRET = "LLNmHsQDCerVWx5d6aCjug9fyPE";
    public static String userName = "useradmin";
    public static String password = "useradmin";
    private static URI tokenServiceUri;
    private static ApplicationCredential appCredential;
    private static UserCredential userCredential;
    private static boolean systemTest = false;
    private static URI userAdminServiceUri;
    private static String userAdminService = "http://localhost:9992/useradminservice";
    private static String userTokenService = "http://localhost:9998/tokenservice";


    @BeforeClass
    public static void setup() throws Exception {
        appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
        tokenServiceUri = UriBuilder.fromUri(userTokenService).build();
        userCredential = new UserCredential(userName, password);

        userAdminServiceUri = UriBuilder.fromUri(userAdminService).build();

        if (systemTest) {
            tokenServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/tokenservice/").build();
            userAdminServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/useradminservice/").build();
            SSLTool.disableCertificateValidation();
        }
    }

    public static String getDummyApplicationJson() {
        return ApplicationHelper.getDummyApplicationJson();
    }

    @Test
    public void testAddApplication() throws Exception {

        if (!SystemTestUtil.noLocalWhydahRunning() || systemTest) {


            System.out.printf("Adding application:\n" + ApplicationMapper.toPrettyJson(ApplicationMapper.fromJson(getDummyApplicationJson())));
            String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            assertTrue(userTokenId != null && userTokenId.length() > 5);

            int existingApplications = countApplications(myApplicationTokenID, userTokenId);

            Application newApplication = ApplicationMapper.fromJson(ApplicationHelper.getDummyApplicationJson());
            String applicationJson = ApplicationMapper.toJson(newApplication);
            String testAddApplication = new CommandAddApplication(userAdminServiceUri, myApplicationTokenID, userTokenId, applicationJson).execute();
            System.out.println("Applications found:" + countApplications(myApplicationTokenID, userTokenId));
            assertTrue(existingApplications == (countApplications(myApplicationTokenID, userTokenId) - 1));


        }

    }

    private int countApplications(String myApplicationTokenID, String userTokenId) {
        String applicationsJson = new CommandListApplications(userAdminServiceUri, myApplicationTokenID, userTokenId, "").execute();
        System.out.println("applicationsJson=" + applicationsJson);
        assertTrue(applicationsJson.length() > 100);
        List<Application> applications = ApplicationMapper.fromJsonList(applicationsJson);
        assertTrue(applications.size() > 2);
        return applications.size();


    }
}