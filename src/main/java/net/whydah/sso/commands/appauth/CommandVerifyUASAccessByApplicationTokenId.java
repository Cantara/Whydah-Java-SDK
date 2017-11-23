package net.whydah.sso.commands.appauth;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommandForBooleanType;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.ddd.model.user.UserTokenId;

import java.net.HttpURLConnection;
import java.net.URI;

public class CommandVerifyUASAccessByApplicationTokenId extends BaseHttpGetHystrixCommandForBooleanType {


	int retryCnt = 0;
	String userTokenId="";

	public CommandVerifyUASAccessByApplicationTokenId(String UASUri, String applicationTokenId) {
		this(UASUri, applicationTokenId, "");
	}
	
	public CommandVerifyUASAccessByApplicationTokenId(String UASUri, String applicationTokenId, String userTokenId) {
		super(URI.create(UASUri), "", applicationTokenId, "STSApplicationAuthGroup");

		if (UASUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(userTokenId)) {
			log.error(TAG + " initialized with null-values - will fail - tokenServiceUri={}, applicationTokenId={}, userTokenId:{}", UASUri, applicationTokenId, userTokenId);
		}
		this.userTokenId = userTokenId;
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
		if(response.contains("true")){
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected Boolean getFallback() {
		return false;
	}


	@Override
	protected String getTargetPath() {
		return myAppTokenId + (userTokenId==null||userTokenId.equals("")?"":"/" + userTokenId) + "/hasUASAccess";
	}
}
