package net.whydah.sso.commands.adminapi.application;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class CommandGetApplicationTest {

    private static final Logger log = LoggerFactory.getLogger(CommandGetApplicationTest.class);

    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
        //config.setLocalTest();
    }


    @Test
    public void testListApplicationsCommand() throws Exception {
        if (config.isSystemTestEnabled()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            String applicationId = config.appCredential.getApplicationID();
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);

            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            assertTrue(userTokenId != null && userTokenId.length() > 5);

            String applicationJson = new CommandGetApplicationById(config.userAdminServiceUri, myApplicationTokenID, userTokenId,applicationId ).execute();
            log.debug("applicationJson=" + applicationJson);
            assertTrue(applicationJson.length() > 100);
            JSONAssert.assertEquals("{\"id\":\"" + SystemTestBaseConfig.TEMPORARY_APPLICATION_ID + "\",\"name\":\"" + SystemTestBaseConfig.TEMPORARY_APPLICATION_NAME + "\"}", applicationJson, false);

        }
    }


}
