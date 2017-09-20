package net.whydah.sso.commands.adminapi.user;

import java.net.URI;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;


public class CommandListUsers extends BaseHttpGetHystrixCommand<String> {
   

    private String adminUserTokenId;
    private String userQuery;


    public CommandListUsers(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userQuery) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup", 3000);
    
        this.adminUserTokenId = adminUserTokenId;
        if (userQuery == null || userQuery.length() < 1) {
            userQuery = "*";
        }
        this.userQuery = userQuery;
        if (userAdminServiceUri == null || myAppTokenId == null || myAppTokenId.length() < 4 || adminUserTokenId == null || adminUserTokenId.length() < 4 || userQuery == null) {
            log.error("CommandListUsers initialized with null-values - will fail - userAdminServiceUri:{}, myAppTokenId:{}, adminUserTokenId:{}, userQuery:{}", userAdminServiceUri, myAppTokenId, adminUserTokenId, userQuery);

        }


    }


	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + adminUserTokenId + "/users/find/" + userQuery;
	}


}
