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
import net.whydah.sso.util.SystemTestUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class CommandAddApplicationTest {

    public static String TEMPORARY_APPLICATION_ID = "11";//"11";
    public static String TEMPORARY_APPLICATION_SECRET = "6r46g3q986Ep6By7B9J46m96D";
    public static String userName = "admin";
    public static String password = "whydahadmin";
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
            tokenServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.nom/tokenservice/").build();
            userAdminServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/tokenservice/").build();
        }
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

            Application newApplication = ApplicationMapper.fromJson(ApplicationHelper.getDummyApplicationJson());
            String applicationJson = ApplicationMapper.toJson(newApplication);
            String testAddApplication = new CommandAddApplication(userAdminServiceUri, myApplicationTokenID, userTokenId, applicationJson).execute();
            System.out.println("testAddApplication:" + testAddApplication);

        }

    }

}