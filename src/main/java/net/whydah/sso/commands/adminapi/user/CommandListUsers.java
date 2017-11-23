package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.ddd.model.user.UserTokenId;

import java.net.URI;


public class CommandListUsers extends BaseHttpGetHystrixCommand<String> {
   

    private String adminUserTokenId;
    private String userQuery;


    public CommandListUsers(URI userAdminServiceUri, String applicationTokenId, String adminUserTokenId, String userQuery) {
        super(userAdminServiceUri, "", applicationTokenId, "UASUserAdminGroup", 3000);

        if (userAdminServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(adminUserTokenId) || userQuery == null) {
            log.error("CommandListUsers initialized with null-values - will fail - userAdminServiceUri:{}, myAppTokenId:{}, adminUserTokenId:{}, userQuery:{}", userAdminServiceUri, applicationTokenId, adminUserTokenId, userQuery);

        }
        this.adminUserTokenId = adminUserTokenId;
        if (userQuery == null || userQuery.length() < 1) {
            userQuery = "*";
        }
        this.userQuery = userQuery;


    }


	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + adminUserTokenId + "/users/find/" + userQuery;
	}


}
