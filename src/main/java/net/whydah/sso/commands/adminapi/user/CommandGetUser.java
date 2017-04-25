package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;

public class CommandGetUser extends BaseHttpGetHystrixCommand<String> {
 
    private String adminUserTokenId;
    private String userID;


    public CommandGetUser(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userID) {
    	super(userAdminServiceUri, "", myAppTokenId,"UASUserAdminGroup", 3000);
    	
        
        this.adminUserTokenId = adminUserTokenId;
        this.userID = userID;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userID == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }

    }

//    @Override
//    protected String run() {
//        log.trace("CommandGetUser - myAppTokenId={}", myAppTokenId);
//
//        Client uasClient = ClientBuilder.newClient();
//
//        WebTarget getUser = uasClient.target(userAdminServiceUri).path(myAppTokenId).path(adminUserTokenId).path("user").path(userID);
//        Response response = getUser.request().get();
//        if (response.getStatus() == OK.getStatusCode()) {
//            String responseJson = response.readEntity(String.class);
//            log.debug("CommandGetUser - Returning user {}", responseJson);
//            return responseJson;
//        }
//        return null;
//
//
//    }
//
//    @Override
//    protected String getFallback() {
//        log.warn("CommandGetUser - fallback - whydahServiceUri={}", userAdminServiceUri.toString());
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + adminUserTokenId + "/user/" + userID;
	}


}
