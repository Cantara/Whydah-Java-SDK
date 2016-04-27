package net.whydah.sso.application;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.user.types.UserToken;
import net.whydah.sso.util.SSLTool;

import java.net.URI;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SystemTestBaseConfig {

    public String TEMPORARY_APPLICATION_ID = "100";//"11";
    public String TEMPORARY_APPLICATION_NAME = "Whydah-SystemTests";//"Funny APp";//"11";
    public String TEMPORARY_APPLICATION_SECRET = "45fhRM6nbKZ2wfC6RMmMuzXpk";//"LLNmHsQDCerVWx5d6aCjug9fyPE";
    public String userName = "useradmin";
    public String password = "useradmin42";
    public URI tokenServiceUri;
    public URI userAdminServiceUri;
    public String userAdminService = "http://localhost:9992/useradminservice";
    public String userTokenService = "http://localhost:9998/tokenservice";
    public boolean statisticsExtensionSystemTest = true;
    public boolean CRMCustomerExtensionSystemTest = true;
    public boolean systemTest = true;
    public ApplicationCredential appCredential;
    public UserCredential userCredential;
    
    
    public URI statisticsServiceUri;
    public URI crmServiceUri;

    public ApplicationToken myApplicationToken;

    public SystemTestBaseConfig() {
        appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
        tokenServiceUri = URI.create(userTokenService);
         userCredential = new UserCredential(userName, password);
        userAdminServiceUri = URI.create(userAdminService);

         if (systemTest) {
             SSLTool.disableCertificateValidation();
             tokenServiceUri = URI.create("https://whydahdev.cantara.no/tokenservice/");
             userAdminServiceUri = URI.create("https://whydahdev.cantara.no/useradminservice/");

             crmServiceUri = URI.create("https://whydahdev.cantara.no/crmservice/");
             statisticsServiceUri = URI.create("https://whydahdev.cantara.no/reporter/");
             
         }
    }

    public boolean isSystemTestEnabled() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {

        }
        return systemTest;
    }

    public boolean isStatisticsExtensionSystemtestEnabled() {
        return statisticsExtensionSystemTest;
    }

    public boolean isCRMCustomerExtensionSystemTestEnabled() {
        return CRMCustomerExtensionSystemTest;
    }


    public ApplicationToken logOnSystemTestApplication() {
        if (isCRMCustomerExtensionSystemTestEnabled()) {
            String myApplicationTokenID = "";
            SSLTool.disableCertificateValidation();
            ApplicationCredential appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
            String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
            myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue("Unable to log on application ", myApplicationTokenID.length() > 10);

            ApplicationToken appToken = ApplicationTokenMapper.fromXml(myAppTokenXml);
            assertNotNull(appToken);
            myApplicationToken = appToken;

            return appToken;
        }
        return null;
    }

    public UserToken logOnSystemTestApplicationAndSystemTestUser() {
        if (isCRMCustomerExtensionSystemTestEnabled()) {
            String myApplicationTokenID = "";
            SSLTool.disableCertificateValidation();
            ApplicationCredential appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
            String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
            myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue("Unable to log on application ", myApplicationTokenID.length() > 10);

            ApplicationToken appToken = ApplicationTokenMapper.fromXml(myAppTokenXml);
            assertNotNull(appToken);
            myApplicationToken = appToken;

            String userticket = UUID.randomUUID().toString();
            String userTokenXML = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userTokenXML);
            assertTrue("Unable to log on user", userTokenId.length() > 10);
            UserToken userToken = UserTokenMapper.fromUserTokenXml(userTokenXML);
            assertNotNull(userToken);
            return userToken;
        }
        return null;
    }

}
