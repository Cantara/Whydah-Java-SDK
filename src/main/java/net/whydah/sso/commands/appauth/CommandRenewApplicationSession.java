package net.whydah.sso.commands.appauth;


import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.commands.baseclasses.HttpSender;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;

import java.net.URI;

public class CommandRenewApplicationSession extends BaseHttpPostHystrixCommand<String> {


    public CommandRenewApplicationSession(URI tokenServiceUri, String applicationTokenId, int millisecondwait) {
        super(tokenServiceUri, "", applicationTokenId, "STSApplicationAuthGroup", millisecondwait);


        if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId)) {
            log.error(TAG + " initialized with null-values - will fail - tokenServiceUri={}, applicationtokenid={}", tokenServiceUri, applicationTokenId);
            throw new IllegalArgumentException("Missing parameters for \n" +
                    "\ttokenServiceUri [" + tokenServiceUri + "], \n" +
                    "\tapplicationtokenid [" + applicationTokenId + "]");
        }

    }

    public CommandRenewApplicationSession(URI tokenServiceUri, String applicationTokenId) {
        super(tokenServiceUri, "", applicationTokenId, "STSApplicationAuthGroup", 3000);


        if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId)) {
            log.error(TAG + " initialized with null-values - will fail - tokenServiceUri={}, applicationtokenid={}", tokenServiceUri, applicationTokenId);
            throw new IllegalArgumentException("Missing parameters for \n" +
                    "\ttokenServiceUri [" + tokenServiceUri + "], \n" +
                    "\tapplicationtokenid [" + applicationTokenId + "]");
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