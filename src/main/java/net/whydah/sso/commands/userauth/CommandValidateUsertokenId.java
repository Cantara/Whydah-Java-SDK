package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.HttpURLConnection;
import java.net.URI;

public class CommandValidateUsertokenId extends BaseHttpGetHystrixCommand<Boolean> {

    private String usertokenid;

    public CommandValidateUsertokenId(URI tokenServiceUri, String myAppTokenId, String usertokenid) {
    	super(tokenServiceUri, "", myAppTokenId, "SSOUserAuthGroup", 3000);
        this.usertokenid=usertokenid;
        if (tokenServiceUri == null || myAppTokenId == null || usertokenid == null  ) {
            log.error("CommandValidateUsertokenId initialized with null-values - will fail");
        }
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
