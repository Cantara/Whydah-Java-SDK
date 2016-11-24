package net.whydah.sso.commands.adminapi.application;


import com.github.kevinsawicki.http.HttpRequest;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.commands.baseclasses.BaseHttpPutHystrixCommand;

import java.net.URI;

// TODO:  wait for https://github.com/Cantara/Whydah-UserAdminService/issues/35

public class CommandUpdateApplication extends BaseHttpPutHystrixCommand<String> {

  
    
    private String adminUserTokenId;
    private String applicationJson;
    private String applicationId;

    public CommandUpdateApplication(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String applicationId, String applicationJson) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup");
        
    
        this.adminUserTokenId = adminUserTokenId;
        this.applicationJson = applicationJson;
        this.applicationId = applicationId;
        
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || applicationJson == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }

    }
    
    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
    	return request.contentType("application/json").send(applicationJson);
    }
    
    @Override
    protected String dealWithFailedResponse(String responseBody, int statusCode) {
    	if(statusCode == 204){
    		return applicationJson;
    	}
    	return null;
    }
    
    @Override
    protected String getTargetPath() {
    	return myAppTokenId + "/" + adminUserTokenId + "/application/" + applicationId;
    }


}
