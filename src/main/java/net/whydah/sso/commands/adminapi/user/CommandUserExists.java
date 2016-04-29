package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;

public class CommandUserExists extends BaseHttpGetHystrixCommand<Boolean> {
  
    private String adminUserTokenId;
    private String userQuery;


    public CommandUserExists(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userQuery) {
    	super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup");
       
        this.adminUserTokenId = adminUserTokenId;
        this.userQuery = userQuery;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userQuery == null) {
            log.error("CommandUserExists initialized with null-values - will fail");
            log.error("CommandSendSMSToUser initialized with null-values - will fail - userAdminServiceUri:{}, myAppTokenId:{}, adminUserTokenId:{}, userQuery:{}", userAdminServiceUri, myAppTokenId, adminUserTokenId, userQuery);

        }

    }
    
    @Override
    protected Boolean dealWithResponse(String response) {
    	return response.length() > 32;
    }

//    @Override
//    protected Boolean run() {
//
//        log.trace("CommandUserExists - myAppTokenId={}", myAppTokenId);
//        Client uasClient = ClientBuilder.newClient();
//
//        WebTarget userDirectory = uasClient.target(userAdminServiceUri).path(myAppTokenId).path(adminUserTokenId).path("users").path("find/").path(userQuery);
//
//        // Works against UIB, still misisng in UAS...
//        Response response = userDirectory.request().get();
//        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
//            log.info("CommandUserExists -  userDirectory failed with status code " + response.getStatus());
//            return false;
//            //throw new IllegalArgumentException("Log on failed. " + ClientResponse.Status.FORBIDDEN);
//        }
//        if (response.getStatus() == OK.getStatusCode()) {
//            String responseJson = response.readEntity(String.class);
//            log.debug("CommandUserExists - Listing users {}", responseJson);
//            return responseJson.length() > 32;
//        }
//
//        return false;
//    }
//
//
//    @Override
//    protected Boolean getFallback() {
//        log.warn("CommandUserExists - fallback - whydahServiceUri={}", userAdminServiceUri.toString());
//        return false;
//    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + adminUserTokenId + "/users" + "/find/" + userQuery;
	}


}
