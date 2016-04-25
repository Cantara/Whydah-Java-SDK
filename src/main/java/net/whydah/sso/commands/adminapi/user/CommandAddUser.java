package net.whydah.sso.commands.adminapi.user;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.URI;

public class CommandAddUser extends BaseHttpPostHystrixCommand<String> {
    
    private String adminUserTokenId;
    //private UserCredential userCredential;
    private String userIdentityJson;

    public CommandAddUser(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userIdentityJson) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup");

        this.adminUserTokenId=adminUserTokenId;
        this.userIdentityJson =userIdentityJson;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userIdentityJson==null ) {
            log.error(TAG + " initialized with null-values - will fail - userAdminServiceUri:{}, myAppTokenId:{}, adminUserTokenId:{}, userIdentityJson:{}", userAdminServiceUri, myAppTokenId, adminUserTokenId, userIdentityJson);
        }
    }
//
//    @Override
//    protected String run() {
//
//        log.trace(TAG + " - myAppTokenId={}, adminUserTokenId={{}, userIdentityJson={}", myAppTokenId, adminUserTokenId, userIdentityJson);
//        Client tokenServiceClient = ClientBuilder.newClient();
//        WebTarget addUser = tokenServiceClient.target(userAdminServiceUri).path(myAppTokenId).path(adminUserTokenId).path("user");
//        Response response = addUser.request().post(Entity.json(userIdentityJson));
//        if (response.getStatus() == OK.getStatusCode()) {
//            String responseXML = response.readEntity(String.class);
//            log.info("CommandAddUser - addUser - Create user OK with response {}", responseXML);
//            return responseXML;
//        }
//
//        log.warn("CommandAddUser - addUser - failed with status code " + response.getStatus());
//        return null;
//
//    }
    
    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
    
    	return request.contentType("application/json").send(userIdentityJson);
    	
    }

//    @Override
//    protected String getFallback() {
//        log.warn("CommandAddUser - fallback - whydahServiceUri={}", userAdminServiceUri.toString());
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + adminUserTokenId  +  "/user";
	}


}
