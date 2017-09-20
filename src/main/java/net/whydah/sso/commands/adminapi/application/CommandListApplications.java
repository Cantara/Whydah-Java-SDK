package net.whydah.sso.commands.adminapi.application;

import java.net.HttpURLConnection;
import java.net.URI;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import com.github.kevinsawicki.http.HttpRequest;

public class CommandListApplications extends BaseHttpGetHystrixCommand<String> {

    int retryCnt = 0;


    public CommandListApplications(URI userAdminServiceUri, String myAppTokenId) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup", 3000);
        if (userAdminServiceUri == null || myAppTokenId == null) {
            log.error(TAG + " initialized with null-values - will fail - userAdminServiceUri={}, myAppTokenId={}", userAdminServiceUri, myAppTokenId);
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
