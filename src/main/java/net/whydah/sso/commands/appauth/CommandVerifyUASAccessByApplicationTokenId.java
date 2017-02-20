package net.whydah.sso.commands.appauth;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.HttpURLConnection;
import java.net.URI;

public class CommandVerifyUASAccessByApplicationTokenId extends BaseHttpGetHystrixCommand<Boolean> {


	int retryCnt = 0;


	public CommandVerifyUASAccessByApplicationTokenId(String UASUri, String applicationTokenId) {
		super(URI.create(UASUri), "", applicationTokenId, "STSApplicationAuthGroup");

		if (UASUri == null || applicationTokenId == null) {
			log.error(TAG + " initialized with null-values - will fail - tokenServiceUri={}, applicationTokenId={}", UASUri, applicationTokenId);
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
		return myAppTokenId + "/hasUASAccess";
	}
}
