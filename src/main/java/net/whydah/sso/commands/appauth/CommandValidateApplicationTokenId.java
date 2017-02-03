package net.whydah.sso.commands.appauth;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.HttpURLConnection;
import java.net.URI;

public class CommandValidateApplicationTokenId extends BaseHttpGetHystrixCommand<Boolean> {


	int retryCnt = 0;


	public CommandValidateApplicationTokenId(String tokenServiceUri, String applicationTokenId) {
		super(URI.create(tokenServiceUri), "", applicationTokenId, "STSApplicationAuthGroup");

		if (tokenServiceUri == null || applicationTokenId == null) {
			log.error(TAG + " initialized with null-values - will fail - tokenServiceUri={}, applicationTokenId={}", tokenServiceUri, applicationTokenId);
		}
	}


	@Override
	protected Boolean dealWithFailedResponse(String responseBody, int statusCode) {
		if(statusCode != HttpURLConnection.HTTP_CONFLICT && retryCnt<1){
			retryCnt++;
			return doGetCommand();
		} else {
			return false;
		}
	}

	@Override
	protected Boolean dealWithResponse(String response) {
		return true;
	}

	@Override
	protected Boolean getFallback() {
		return false;
	}


	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/validate";
	}
}
