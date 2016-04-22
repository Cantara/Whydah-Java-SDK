package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.URI;

/**
 * Created by totto on 24.06.15.
 */
//TODO: Check, it is unfinished
public class CommandUpdateUserRole extends BaseHttpPostHystrixCommand<String> {

    private String adminUserTokenId;
    private String userRoleJson;


    public CommandUpdateUserRole(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userRoleJson) {
    	super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup");
        
        this.adminUserTokenId = adminUserTokenId;
        this.userRoleJson = userRoleJson;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userRoleJson == null) {
            log.error("CommandUpdateUserRole initialized with null-values - will fail");
        }

    }

//    @Override
//    protected String run() {
//        log.trace("CommandUpdateUserRole - myAppTokenId={}", myAppTokenId);
//
//        Client tokenServiceClient = ClientBuilder.newClient();
//
//        WebTarget addUser = tokenServiceClient.target(userAdminServiceUri).path(myAppTokenId).path(adminUserTokenId).path("/xxx");
//        // Response response = addUser.request().post(Entity.xml(userIdentityXml));
//        throw new UnsupportedOperationException();
//        //return null;
//
//    }

//    @Override
//    protected String getFallback() {
//        log.warn("CommandUpdateUserRole - fallback - whydahServiceUri={}", userAdminServiceUri.toString());
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + adminUserTokenId + "/xxx";
	}


}
