package net.whydah.sso.commands.appauth;


import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.commands.baseclasses.HttpSender;

import java.net.URI;

public class CommandRenewApplicationSession extends BaseHttpPostHystrixCommand<String> {


    public CommandRenewApplicationSession(URI tokenServiceUri, String applicationtokenid, int millisecondwait) {
        super(tokenServiceUri, "", applicationtokenid, "STSApplicationAuthGroup", millisecondwait);


        if (tokenServiceUri == null || applicationtokenid == null) {
            log.error(TAG + " initialized with null-values - will fail - tokenServiceUri={}, applicationtokenid={}", tokenServiceUri, applicationtokenid);
            throw new IllegalArgumentException("Missing parameters for \n" +
                    "\ttokenServiceUri [" + tokenServiceUri + "], \n" +
                    "\tapplicationtokenid [" + applicationtokenid + "]");
        }

    }

    public CommandRenewApplicationSession(URI tokenServiceUri, String applicationtokenid) {
        super(tokenServiceUri, "", applicationtokenid, "STSApplicationAuthGroup", 3000);


        if (tokenServiceUri == null || applicationtokenid == null) {
            log.error(TAG + " initialized with null-values - will fail - tokenServiceUri={}, applicationtokenid={}", tokenServiceUri, applicationtokenid);
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