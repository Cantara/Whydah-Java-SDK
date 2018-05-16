package net.whydah.sso.commands.application;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;

import java.net.HttpURLConnection;
import java.net.URI;

public class CommandListApplications extends BaseHttpGetHystrixCommand<String> {

    int retryCnt = 0;

    public static int DEFAULT_TIMEOUT = 6000;
    
    public CommandListApplications(URI userAdminServiceUri, String applicationTokenId) {
        super(userAdminServiceUri, "", applicationTokenId, "UASUserAdminGroup", DEFAULT_TIMEOUT);
        if (userAdminServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId)) {
            log.error(TAG + " initialized with null-values - will fail - userAdminServiceUri={}, myAppTokenId={}", userAdminServiceUri, applicationTokenId);
        }
    }

    public CommandListApplications(URI userAdminServiceUri, String applicationTokenId, int timeout) {
        super(userAdminServiceUri, "", applicationTokenId, "UASUserAdminGroup", timeout);
        if (userAdminServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId)) {
            log.error(TAG + " initialized with null-values - will fail - userAdminServiceUri={}, myAppTokenId={}", userAdminServiceUri, applicationTokenId);
        }
    }


    @Override
    protected String dealWithFailedResponse(String responseBody, int statusCode) {
        if (statusCode != HttpURLConnection.HTTP_CONFLICT && retryCnt < 1) {
            retryCnt++;
            return doGetCommand();
        } else {
            return null;
        }
    }

    @Override
    protected String getTargetPath() {
        return myAppTokenId + "/applications";
    }

     @Override
	protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
		return super.dealWithRequestBeforeSend(request);
	}

}
