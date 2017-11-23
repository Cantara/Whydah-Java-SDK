package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommandForBooleanType;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.ddd.model.user.UserTokenId;

import java.net.HttpURLConnection;
import java.net.URI;

public class CommandValidateUsertokenId extends BaseHttpGetHystrixCommandForBooleanType {

    private String usertokenid;

    public CommandValidateUsertokenId(URI tokenServiceUri, String applicationTokenId, String userTokenId) {
        super(tokenServiceUri, "", applicationTokenId, "SSOUserAuthGroup", 3000);
        if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(userTokenId)) {
            log.error("CommandValidateUsertokenId initialized with null-values - will fail");
        }
        this.usertokenid = userTokenId;
    }


	int retryCnt=0;
    
    @Override
    protected Boolean dealWithFailedResponse(String responseBody, int statusCode) {
    	if(statusCode == HttpURLConnection.HTTP_CONFLICT){
    		return false;
    	} else {
    		
    		if(retryCnt<1){
    			retryCnt++;
    			return doGetCommand();
    		} else {
    			return false;
    		}
    		
    	}
    }
    
    @Override
    protected Boolean dealWithResponse(String response) {
    	return true;
    }

	@Override
	protected String getTargetPath() {
		return "user/" + myAppTokenId + "/validate_usertokenid/" + usertokenid;
	}


}
