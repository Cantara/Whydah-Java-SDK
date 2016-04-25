package net.whydah.sso.application;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SSLTool;
import net.whydah.sso.util.SystemTestUtil;

public class BaseConfig {
	
	public String TEMPORARY_APPLICATION_NAME = "Whydah-SSOLoginWebApp";//"Funny APp";//"11";
	public String TEMPORARY_APPLICATION_ID = "2215";//"11";
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
         tokenServiceUri = UriBuilder.fromUri(userTokenService).build();
         userCredential = new UserCredential(userName, password);
         userAdminServiceUri = UriBuilder.fromUri(userAdminService).build();

         if (systemTest) {
             tokenServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/tokenservice/").build();
             userAdminServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/useradminservice/").build();
             
             crmServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/crmservice/").build();
             statisticsServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/reporter/").build();
             
             SSLTool.disableCertificateValidation();
         }
    }
    
    public boolean enableTesting(){
    	return !SystemTestUtil.noLocalWhydahRunning() || systemTest;
    }
    
}
