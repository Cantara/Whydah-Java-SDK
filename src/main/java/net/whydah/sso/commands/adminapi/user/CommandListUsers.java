package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;


public class CommandListUsers extends BaseHttpGetHystrixCommand<String> {
   

    private String adminUserTokenId;
    private String userQuery;


    public CommandListUsers(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userQuery) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup", 3000);
    
        this.adminUserTokenId = adminUserTokenId;
        this.userQuery = userQuery;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userQuery == null) {
            log.error(TAG + "initialized with null-values - will fail");
        }

    }

//    @Override
//    protected String run() {
//
//        log.trace("CommandListUsers - myAppTokenId={}", myAppTokenId);
//        String uasAdminApiUrl = userAdminServiceUri.toString() + myAppTokenId + "/" + adminUserTokenId + "/users/find/" + userQuery;
//
//        HttpRequest request = HttpRequest.get(uasAdminApiUrl);
//        int statusCode = request.code();
//        String responseBody = request.body();
//        switch (statusCode) {
//            case HttpSender.STATUS_OK:
//                log.debug("CommandListUsers - {}", responseBody);
//                return responseBody;
//            default:
//                log.warn("Unexpected response from UAS. Response is {} content is {}", responseBody, responseBody);
//
//        }
//        throw new RuntimeException("CommandListUsers - Operation failed");
//
//
//    }

//
//    @Override
//    protected String getFallback() {
//        log.warn("CommandListUsers - fallback - whydahServiceUri={}", userAdminServiceUri.toString());
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + adminUserTokenId + "/users/find/" + userQuery;
	}


}
