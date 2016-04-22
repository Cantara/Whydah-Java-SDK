package net.whydah.sso.commands.adminapi.application;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;


public class CommandListApplications extends BaseHttpGetHystrixCommand<String> {
    
    
    private String adminUserTokenId;
    private String applicationQuery;


    public CommandListApplications(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String applicationQuery) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup", 300000);
        this.adminUserTokenId = adminUserTokenId;
        this.applicationQuery = applicationQuery;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || applicationQuery == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }
    }
    
	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + adminUserTokenId + "/applications";
	}

	@Override
	protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
		// return request.contentType("application/json").send(applicationQuery);
		return super.dealWithRequestBeforeSend(request);
	}
	

//	 @Override
//	    protected String run() {
//	        log.trace("CommandListApplications - myAppTokenId={}", myAppTokenId);
//	        Client uasClient = ClientBuilder.newClient();
//
//	        WebTarget applicationList = uasClient.target(userAdminServiceUri).path(myAppTokenId + "/" + adminUserTokenId + "/applications");
//
//	        Response response = applicationList.request().get();
//	        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
//	            log.info("CommandListApplications -  User authentication failed with status code " + response.getStatus());
//	            return null;
//	            //throw new IllegalArgumentException("Log on failed. " + ClientResponse.Status.FORBIDDEN);
//	        }
//	        if (response.getStatus() == OK.getStatusCode()) {
//	            String responseJson = response.readEntity(String.class);
//	            log.debug("CommandListApplications - Listing applications {}", responseJson);
//	            return responseJson;
//	        }
//
//	        return null;
//	    }
//
//	    @Override
//	    protected String getFallback() {
//	        log.warn("CommandListApplications - fallback - whydahServiceUri={}", userAdminServiceUri.toString());
//	        return null;
//	    }
//	    
}
