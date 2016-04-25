package net.whydah.sso.application;

import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SSLTool;
import net.whydah.sso.util.SystemTestUtil;

import java.net.URI;

public class BaseConfig {

    public String TEMPORARY_APPLICATION_ID = "2215";//"11";
    public String TEMPORARY_APPLICATION_NAME = "Whydah-SSOLoginWebApp";//"Funny APp";//"11";
    public String TEMPORARY_APPLICATION_SECRET = "33779936R6Jr47D4Hj5R6p9qT";//"LLNmHsQDCerVWx5d6aCjug9fyPE";
    public String userName = "useradmin";
    public String password = "useradmin42";
    public URI tokenServiceUri;
    public URI userAdminServiceUri;
    public String userAdminService = "http://localhost:9992/useradminservice";
    public String userTokenService = "http://localhost:9998/tokenservice";
    public boolean systemTest = true;
    public ApplicationCredential appCredential;
    public UserCredential userCredential;
    
    
    public URI statisticsServiceUri;
    public URI crmServiceUri;
    
    public BaseConfig(){
    	 appCredential = new ApplicationCredential(TEMPORARY_APPLICATION_ID, TEMPORARY_APPLICATION_NAME, TEMPORARY_APPLICATION_SECRET);
        tokenServiceUri = URI.create(userTokenService);
         userCredential = new UserCredential(userName, password);
        userAdminServiceUri = URI.create(userAdminService);

         if (systemTest) {
             tokenServiceUri = URI.create("https://whydahdev.cantara.no/tokenservice/");
             userAdminServiceUri = URI.create("https://whydahdev.cantara.no/useradminservice/");

             crmServiceUri = URI.create("https://whydahdev.cantara.no/crmservice/");
             statisticsServiceUri = URI.create("https://whydahdev.cantara.no/reporter/");
             
             SSLTool.disableCertificateValidation();
         }
    }
    
    public boolean enableTesting(){
    	return !SystemTestUtil.noLocalWhydahRunning() || systemTest;
    }
    
}
