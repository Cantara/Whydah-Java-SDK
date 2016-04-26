package net.whydah.sso.application;

import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SSLTool;

import java.net.URI;

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
}
