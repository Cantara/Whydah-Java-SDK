package net.whydah.sso.commands.appauth;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommandForBooleanType;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;

import java.net.HttpURLConnection;
import java.net.URI;

public class CommandValidateApplicationTokenId extends BaseHttpGetHystrixCommandForBooleanType {

	public static int DEFAULT_TIMEOUT = 6000;
	int retryCnt = 0;


	public CommandValidateApplicationTokenId(String tokenServiceUri, String applicationTokenId) {
		super(URI.create(tokenServiceUri), "", applicationTokenId, "STSApplicationAuthGroup", DEFAULT_TIMEOUT);

		if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId)) {
			log.error(TAG + " initialized with null-values - will fail - tokenServiceUri={}, applicationTokenId={}", tokenServiceUri, applicationTokenId);
		}
	}

    public CommandValidateApplicationTokenId(URI tokenServiceUri, String applicationTokenId) {
        super(tokenServiceUri, "", applicationTokenId, "STSApplicationAuthGroup", DEFAULT_TIMEOUT);

		if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId)) {
			log.error(TAG + " initialized with null-values - will fail - tokenServiceUri={}, applicationTokenId={}", tokenServiceUri, applicationTokenId);
        }
    }
    
	public CommandValidateApplicationTokenId(String tokenServiceUri, String applicationTokenId, int timeout) {
		super(URI.create(tokenServiceUri), "", applicationTokenId, "STSApplicationAuthGroup", timeout);

		if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId)) {
			log.error(TAG + " initialized with null-values - will fail - tokenServiceUri={}, applicationTokenId={}", tokenServiceUri, applicationTokenId);
		}
	}

    public CommandValidateApplicationTokenId(URI tokenServiceUri, String applicationTokenId, int timeout) {
        super(tokenServiceUri, "", applicationTokenId, "STSApplicationAuthGroup", timeout);

		if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId)) {
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
