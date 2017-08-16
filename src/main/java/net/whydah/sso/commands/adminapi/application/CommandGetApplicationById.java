package net.whydah.sso.commands.adminapi.application;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.HttpURLConnection;
import java.net.URI;


public class CommandGetApplicationById extends BaseHttpGetHystrixCommand<String> {

    private final String adminUserTokenId;
    private final String applicationId;
    int retryCnt = 0;


    public CommandGetApplicationById(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String applicationId) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup", 3000);
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || applicationId == null) {
            log.error(TAG + " initialized with null-values - will fail - userAdminServiceUri={}, myAppTokenId={}, adminUserTokenId={}, applicationId={}", userAdminServiceUri, myAppTokenId, adminUserTokenId, applicationId);
        }
        this.adminUserTokenId = adminUserTokenId;
        this.applicationId = applicationId;
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
        return myAppTokenId + "/" + adminUserTokenId + "/application/" + applicationId;
    }

     @Override
	protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
		return super.dealWithRequestBeforeSend(request);
	}

}
