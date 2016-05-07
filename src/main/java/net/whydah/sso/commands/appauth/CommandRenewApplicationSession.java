package net.whydah.sso.commands.appauth;


import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.commands.baseclasses.HttpSender;

import java.net.URI;

public class CommandRenewApplicationSession extends BaseHttpPostHystrixCommand<String> {

  

    public CommandRenewApplicationSession(URI tokenServiceUri, String applicationtokenid) {
    	super(tokenServiceUri, "", applicationtokenid, "STSApplicationAdminGroup", 3000);
        

        if (tokenServiceUri == null || applicationtokenid == null) {
            log.error("CommandRenewApplicationSession initialized with null-values - will fail");
            throw new IllegalArgumentException("Missing parameters for \n" +
                    "\ttokenServiceUri [" + tokenServiceUri + "], \n" +
                    "\tapplicationtokenid [" + applicationtokenid + "]");
        }
      
    }

    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
        return request.contentType(HttpSender.APPLICATION_FORM_URLENCODED);
    }



	@Override
	protected String getTargetPath() {
		return myAppTokenId+ "/renew_applicationtoken";
	}

}