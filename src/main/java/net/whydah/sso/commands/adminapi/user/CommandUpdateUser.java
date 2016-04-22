package net.whydah.sso.commands.adminapi.user;

import java.net.URI;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import com.github.kevinsawicki.http.HttpRequest;

public class CommandUpdateUser extends BaseHttpPostHystrixCommand<String> {
  
    private String adminUserTokenId;
    private String userJson;


    public CommandUpdateUser(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userJson) {
    	super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup");
        
        this.adminUserTokenId = adminUserTokenId;
        this.userJson = userJson;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userJson == null) {
            log.error("CommandUpdateUser initialized with null-values - will fail");
        }

    }
    
    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
    	return request.contentType("application/json").send(userJson);
    }

//    @Override
//    protected String run() {
//        log.trace("CommandUpdateUser - myAppTokenId={}", myAppTokenId);
//
//        Client uasClient = ClientBuilder.newClient();
//
//        WebTarget updateUser = uasClient.target(userAdminServiceUri).path(myAppTokenId).path(adminUserTokenId).path("/xxx");
//        Response response = updateUser.request().post(Entity.json(userJson));
//        throw new UnsupportedOperationException();
//        //return null;
//
//    }

//    @Override
//    protected String getFallback() {
//        log.warn("CommandUpdateUser - fallback - uri={}", userAdminServiceUri.toString());
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + adminUserTokenId + "/xxx";
	}


}
