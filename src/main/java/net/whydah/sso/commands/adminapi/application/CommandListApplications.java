package net.whydah.sso.commands.adminapi.application;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;


public class CommandListApplications extends BaseHttpGetHystrixCommand<String> {
    
    
    private String applicationQuery;


    public CommandListApplications(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String applicationQuery) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup");
        this.applicationQuery = applicationQuery;
        if (userAdminServiceUri == null || myAppTokenId == null || applicationQuery == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }
    }

    public CommandListApplications(URI userAdminServiceUri, String myAppTokenId, String applicationQuery) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup");
        this.applicationQuery = applicationQuery;
        if (userAdminServiceUri == null || myAppTokenId == null || applicationQuery == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }
    }

    @Override
    protected String getTargetPath() {
        return myAppTokenId + "/applications/" + applicationQuery;
    }

	@Override
	protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
		// return request.contentType("application/json").send(applicationQuery);
		return super.dealWithRequestBeforeSend(request);
	}

}
