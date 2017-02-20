package net.whydah.sso.commands.adminapi.application;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.mappers.ApplicationMapper;
import net.whydah.sso.application.types.Application;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.session.baseclasses.ApplicationModelUtil;
import net.whydah.sso.user.helpers.UserXpathHelper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class CommandListApplicationsTest {

    private static final Logger log = LoggerFactory.getLogger(CommandListApplicationsTest.class);

    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
        //config.setLocalTest();
    }


    @Test
    public void testListApplicationsCommand2() throws Exception {
        if (config.isSystemTestEnabled()) {
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            assertTrue(userTokenId != null && userTokenId.length() > 5);

            String applicationsJsonl = new CommandListApplications(config.userAdminServiceUri, myApplicationTokenID).execute();
            log.debug("applicationsJson=" + applicationsJsonl);
            assertTrue(!applicationsJsonl.isEmpty());

        }

    }

    @Test
    public void testListApplicationsCommand() throws Exception {
        if (config.isSystemTestEnabled()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            assertTrue(userTokenId != null && userTokenId.length() > 5);

            String applicationsJson = new CommandListApplications(config.userAdminServiceUri, myApplicationTokenID).execute();
            log.debug("applicationsJson=" + applicationsJson);
            assertTrue(applicationsJson.length() > 100);
            List<Application> applications = ApplicationMapper.fromJsonList(applicationsJson);
            assertTrue(applications.size() > 6);

        }
    }

    @Test
    public void testListApplicationsCommandAndTestMaxTomeoutparams() throws Exception {
        if (config.isSystemTestEnabled()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            assertTrue(userTokenId != null && userTokenId.length() > 5);

            String applicationsJson = new CommandListApplications(config.userAdminServiceUri, myApplicationTokenID).execute();
            log.debug("applicationsJson=" + applicationsJson);
            assertTrue(applicationsJson.length() > 100);
            List<Application> applications = ApplicationMapper.fromJsonList(applicationsJson);
            assertTrue(applications.size() > 6);
            for (Application application : applications) {
                log.debug(ApplicationMapper.toPrettyJson(application));
                if (application.getSecurity() != null) {
                    log.debug("Application applicationId:{} has getSecurity:{}", application.getId(), application.getSecurity());
                    log.debug("Application applicationId:{} has maxSessionTimeoutSeconds:{}", application.getId(), application.getSecurity().getMaxSessionTimeoutSeconds());
                    log.debug("Parsed: {}", ApplicationModelUtil.getParameterForApplication(ApplicationModelUtil.maxSessionTimeoutSeconds, application.getId()));

                }
            }

        }
    }
}
