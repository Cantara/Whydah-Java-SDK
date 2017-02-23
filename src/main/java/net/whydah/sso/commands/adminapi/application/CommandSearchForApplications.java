package net.whydah.sso.commands.adminapi.application;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;

public class CommandSearchForApplications extends BaseHttpGetHystrixCommand<String> {


    private String applicationQuery;
    private String adminUserTokenId=null;

    @Deprecated
    public CommandSearchForApplications(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String applicationQuery) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup", 3000);
        this.applicationQuery = applicationQuery;
        this.adminUserTokenId = adminUserTokenId;
        if (userAdminServiceUri == null || myAppTokenId == null || applicationQuery == null || adminUserTokenId == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }
    }
    
    public CommandSearchForApplications(URI userAdminServiceUri, String myAppTokenId, String applicationQuery) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup", 3000);
        this.applicationQuery = applicationQuery;
        if (userAdminServiceUri == null || myAppTokenId == null || applicationQuery == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }
    }

//     @Path("{userTokenId}/find/applications/{applicationName}")

    @Override
    protected String getTargetPath() {
        return myAppTokenId + (adminUserTokenId==null || adminUserTokenId.equals("")?"":"/" + adminUserTokenId) + "/find/applications/" + applicationQuery;
    }

    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
        // return request.contentType("application/json").send(applicationQuery);
        return super.dealWithRequestBeforeSend(request);
    }

}
