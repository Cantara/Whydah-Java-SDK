package net.whydah.sso.util;

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

import java.net.URI;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SystemTestBaseConfig {

    // Run SystemTests for Whydah Extensions?
    public boolean statisticsExtensionSystemTest = true;
    public boolean CRMCustomerExtensionSystemTest = true;
    public String TEMPORARY_APPLICATION_ID = "101";//"11";
    public String TEMPORARY_APPLICATION_NAME = "Whydah-SystemTests";//"Funny APp";//"11";
    public String TEMPORARY_APPLICATION_SECRET = "55fhRM6nbKZ2wfC6RMmMuzXpk";//"LLNmHsQDCerVWx5d6aCjug9fyPE";
    public String userName = "useradmin";
    public String password = "useradmin42";
    public URI tokenServiceUri;
    public URI userAdminServiceUri;
    public String userAdminService = "https://whydahdev.cantara.no/useradminservice/";
    public String userTokenService = "https://whydahdev.cantara.no/tokenservice/";
    public String crmService = "https://whydahdev.cantara.no/crmservice/";
    public String statisticsService = "https://whydahdev.cantara.no/reporter/";
    public ApplicationCredential appCredential;
    public UserCredential userCredential;
    public URI statisticsServiceUri;
    public URI crmServiceUri;
    public ApplicationToken myApplicationToken;
    public String myAppTokenXml;
    public String myApplicationTokenID;
    // Run the Whydah SystemTests?
    boolean systemTest = true;

    public SystemTestBaseConfig() {
        appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
        userCredential = new UserCredential(userName, password);
        SSLTool.disableCertificateValidation();
        setSystemTest(true);
    }

    public void setSystemTest(boolean isSystemTest){
    	this.systemTest = isSystemTest;  
    	
    	if (this.systemTest) {
           
            tokenServiceUri = URI.create("https://whydahdev.cantara.no/tokenservice/");
            userAdminServiceUri = URI.create("https://whydahdev.cantara.no/useradminservice/");
            crmServiceUri = URI.create("https://whydahdev.cantara.no/crmservice/");
            statisticsServiceUri = URI.create("https://whydahdev.cantara.no/reporter/");
        } else {
        	
            tokenServiceUri = URI.create(userTokenService);
            userAdminServiceUri = URI.create(userAdminService);
            crmServiceUri = URI.create(crmService);
            statisticsServiceUri = URI.create(statisticsService);
        }
    }

    public boolean isSystemTestEnabled() {

        try {
            if (systemTest) {
                Thread.sleep(1500);
            }
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
            
            SSLTool.disableCertificateValidation();
            ApplicationCredential appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
            myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
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
            myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
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
    
    public String generatePin() {
    	java.util.Random generator = new java.util.Random();
        generator.setSeed(System.currentTimeMillis());
        int i = generator.nextInt(10000) % 10000;

        java.text.DecimalFormat f = new java.text.DecimalFormat("0000");
        return f.format(i);

    }

}
