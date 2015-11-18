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
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class CommandAddApplicationTest {

    public static String TEMPORARY_APPLICATION_ID = "11";//"11";
    public static String TEMPORARY_APPLICATION_SECRET = "NNNmHsQDCerVWx5d6aCjug9fyPE";
    public static String userName = "useradmin";
    public static String password = "useradmin";
    static Random ran = new Random();
    private static URI tokenServiceUri;
    private static ApplicationCredential appCredential;
    private static UserCredential userCredential;
    private static boolean systemTest = false;
    private static URI userAdminServiceUri;
    private static String userAdminService = "http://localhost:9992/useradminservice";
    private static String userTokenService = "http://localhost:9998/tokenservice";


    @BeforeClass
    public static void setup() throws Exception {
        appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_SECRET);
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
        return "{\n" +
                "  \"id\" : \"" + ran.nextInt(9999) + "\",\n" +
                "  \"name\" : \"ACS" + ran.nextInt(9999) + "\",\n" +
                "  \"description\" : \"Application description here\",\n" +
                "  \"applicationUrl\" : \"http://my.application.com\",\n" +
                "  \"logoUrl\" : \"http://my.application.com/mylogo.png\",\n" +
                "  \"roles\" : [ {\n" +
                "    \"id\" : \"roleId1\",\n" +
                "    \"name\" : \"roleName1\"\n" +
                "  } ],\n" +
                "  \"defaultRoleName\" : \"Employee\",\n" +
                "  \"organizationNames\" : [ {\n" +
                "    \"id\" : \"orgId\",\n" +
                "    \"name\" : \"organizationName1\"\n" +
                "  }, {\n" +
                "    \"id\" : \"orgidxx\",\n" +
                "    \"name\" : \"defaultOrgName\"\n" +
                "  } ],\n" +
                "  \"defaultOrganizationName\" : \"ACSOrganization\",\n" +
                "  \"security\" : {\n" +
                "    \"minSecurityLevel\" : \"0\",\n" +
                "    \"minDEFCON\" : \"DEFCON5\",\n" +
                "    \"maxSessionTimoutSeconds\" : \"86400\",\n" +
                "    \"allowedIpAddresses\" : [ \"0.0.0.0/0\" ],\n" +
                "    \"userTokenFilter\" : \"true\",\n" +
                "    \"secret\" : \"45fhRM6nbKZ2wfC6RMmMuzXpk\"\n" +
                "  },\n" +
                "  \"acl\" : [ ]\n" +
                "}";
    }

    @Test
    public void testAddApplication() throws Exception {

        if (!SystemTestUtil.noLocalWhydahRunning() || systemTest) {


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
        assertTrue(applications.size() > 6);
        return applications.size();


    }
}