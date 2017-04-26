package net.whydah.sso.commands.adminapi.user;


import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;

public class CommandGetUserAggregate extends BaseHttpGetHystrixCommand<String> {
    
    private String adminUserTokenId;
    private String userID;


    public CommandGetUserAggregate(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userID) {
    	super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup", 1500);
        
        this.adminUserTokenId = adminUserTokenId;
        this.userID = userID;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userID == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }

    }
//
//    @Override
//    protected String run() {
//        log.trace("CommandGetUserAggregate - myAppTokenId={}", myAppTokenId);
//
//        Client uasClient = ClientBuilder.newClient();
//
//        WebTarget updateUser = uasClient.target(userAdminServiceUri).path(myAppTokenId).path(adminUserTokenId).path("useraggregate").path(userID);
//        Response response = updateUser.request().get();
//        if (response.getStatus() == OK.getStatusCode()) {
//            String responseJson = response.readEntity(String.class);
//            log.debug("CommandGetUserAggregate - Returning user {}", responseJson);
//            return responseJson;
//        }
//        return null;
//
//
//    }

//    @Override
//    protected String getFallback() {
//        log.warn("CommandGetUserAggregate - fallback - whydahServiceUri={}", userAdminServiceUri.toString());
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + adminUserTokenId + "/useraggregate" + "/" + userID;
	}


}
