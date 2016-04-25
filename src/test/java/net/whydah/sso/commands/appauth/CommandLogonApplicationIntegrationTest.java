package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.BaseConfig;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.util.SSLTool;
import net.whydah.sso.util.SystemTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

public class CommandLogonApplicationIntegrationTest {
    private static final Logger log = getLogger(CommandLogonApplicationIntegrationTest.class);
    BaseConfig config;
    //    public static final String TEMPORARY_APPLICATION_ID = "201";//"11";
//    public static final String TEMPORARY_APPLICATION_SECRET = "33779936R6Jr47D4Hj5R6p9qT";
//    public static String TEMPORARY_APPLICATION_ID = "2215";//"11";
//    public static String TEMPORARY_APPLICATION_NAME = "Whydah-SSOLoginWebApp";//"Whydah-SSOLoginWebApp";
//    public static String TEMPORARY_APPLICATION_SECRET = "3242342";
//
//    private final String userTokenServiceUri = "https://whydahdev.cantara.no/tokenservice/";
    private String myApplicationTokenID = null;
//    private URI tokenServiceUri = null;
private String myAppTokenXml = null;

    @Before
    public void setUp() throws Exception {
        //tokenServiceUri = URI.create(userTokenServiceUri).build();
    	config = new BaseConfig();

    }

    @Test
    public void testLogonApplication() throws Exception {
        if (!SystemTestUtil.noLocalWhydahRunning()) {
            SSLTool.disableCertificateValidation();
            ApplicationCredential appCredential = new ApplicationCredential(config.TEMPORARY_APPLICATION_ID, config.TEMPORARY_APPLICATION_NAME, config.TEMPORARY_APPLICATION_SECRET);
            appCredential = new ApplicationCredential("02d50dfe-a0b2-4d0e-a17b-8f2287ee8b6d","baardl-test","gggggllasgggakkklaadd");
            myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, appCredential).execute();
            myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);

        }
    }

    //INVALID NOW
//    @Test
//    @Ignore
//    public void testCommandLogic(){
//        SSLTool.disableCertificateValidation();
//        ApplicationCredential appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
//        log.trace("CommandLogonApplication - whydahServiceUri={} appCredential={}", tokenServiceUri.toString(), ApplicationCredentialMapper.toXML(appCredential));
//
//
//        Client tokenServiceClient = ClientBuilder.newBuilder()
//                .sslContext(SSLTool.sc)
//                .build();
//        // Client tokenServiceClient = ClientBuilder.newClient();
//
//        Form formData = new Form();
//        formData.param("applicationcredential", ApplicationCredentialMapper.toXML(appCredential));
//
//        Response response;
//        WebTarget logonResource = tokenServiceClient.target(tokenServiceUri).path("logon");
//        try {
//            response = logonResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED), Response.class);
////            response = postForm(formData, logonResource);
//        } catch (RuntimeException e) {
//            log.error("CommandLogonApplication - logonApplication - Problem connecting to {}", logonResource.toString());
//            log.error(e.toString());
//            throw (e);
//        }
//        if (response.getStatus() != 200) {
//            log.error("CommandLogonApplication - Application authentication failed with statuscode {}", response.getStatus());
//            throw new RuntimeException("CommandLogonApplication - Application authentication failed");
//        } else {
//            String myAppTokenXml = response.readEntity(String.class);
//            log.debug("CommandLogonApplication - Applogon ok: apptokenxml: {}", myAppTokenXml);
//            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
//            log.trace("CommandLogonApplication - myAppTokenId: {}", myApplicationTokenID);
//        }
//
//    }
}