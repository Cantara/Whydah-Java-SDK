package net.whydah.sso.commands.adminapi.application;


import net.whydah.sso.application.BaseConfig;
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

	static BaseConfig config;


    @BeforeClass
    public static void setup() throws Exception {
        config = new BaseConfig();
    }

    public static String getDummyApplicationJson() {
        return ApplicationHelper.getDummyApplicationJson();
    }

    @Test
    public void testAddApplication() throws Exception {

        if (config.enableTesting()) {


            System.out.printf("Adding application:\n" + ApplicationMapper.toPrettyJson(ApplicationMapper.fromJson(getDummyApplicationJson())));
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            assertTrue(userTokenId != null && userTokenId.length() > 5);

            int existingApplications = countApplications(myApplicationTokenID, userTokenId);

            Application newApplication = ApplicationMapper.fromJson(ApplicationHelper.getDummyApplicationJson());
            String applicationJson = ApplicationMapper.toJson(newApplication);
            String testAddApplication = new CommandAddApplication(config.userAdminServiceUri, myApplicationTokenID, userTokenId, applicationJson).execute();
            System.out.println("Applications found:" + countApplications(myApplicationTokenID, userTokenId));
            assertTrue(existingApplications == (countApplications(myApplicationTokenID, userTokenId) - 1));


        }

    }

    private int countApplications(String myApplicationTokenID, String userTokenId) {
        String applicationsJson = new CommandListApplications(config.userAdminServiceUri, myApplicationTokenID, userTokenId, "").execute();
        System.out.println("applicationsJson=" + applicationsJson);
        assertTrue(applicationsJson.length() > 100);
        List<Application> applications = ApplicationMapper.fromJsonList(applicationsJson);
        assertTrue(applications.size() > 2);
        return applications.size();


    }
}