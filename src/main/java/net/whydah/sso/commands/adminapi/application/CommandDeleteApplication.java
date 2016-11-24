package net.whydah.sso.commands.adminapi.application;

import com.github.kevinsawicki.http.HttpRequest;

import net.whydah.sso.commands.baseclasses.BaseHttpDeleteHystrixCommand;
import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.HttpURLConnection;
import java.net.URI;


public class CommandDeleteApplication extends BaseHttpDeleteHystrixCommand<String> {

    int retryCnt = 0;
    String applicationId;
    String tokenId;

    public CommandDeleteApplication(URI userAdminServiceUri, String myAppTokenId, String tokenId, String applicationId) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup", 3000);
        if (userAdminServiceUri == null || myAppTokenId == null || applicationId==null || tokenId ==null) {
            log.error(TAG + " initialized with null-values - will fail - userAdminServiceUri={}, myAppTokenId={}", userAdminServiceUri, myAppTokenId);
        }
        
        this.applicationId = applicationId;
        this.tokenId = tokenId;
    }


    @Override
    protected String getTargetPath() {
        return myAppTokenId + "/" + tokenId + "/application/" + applicationId;
    }
    
    @Override
    protected String dealWithFailedResponse(String responseBody, int statusCode) {
    	if(statusCode == 204){
    		return Boolean.toString(true);
    	}
    	return Boolean.toString(false);
    }

}
