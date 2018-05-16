package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.ddd.model.user.UserTokenId;

import java.net.URI;


public class CommandListUsers extends BaseHttpGetHystrixCommand<String> {
   
	public static int DEFAULT_TIMEOUT = 6000;
	
    private String adminUserTokenId;
    private String userQuery;


    public CommandListUsers(URI userAdminServiceUri, String applicationTokenId, String adminUserTokenId, String userQuery) {
       this(userAdminServiceUri, applicationTokenId, adminUserTokenId, userQuery, DEFAULT_TIMEOUT);
    }
    
    public CommandListUsers(URI userAdminServiceUri, String applicationTokenId, String adminUserTokenId, String userQuery, int timeout) {
        super(userAdminServiceUri, "", applicationTokenId, "UASUserAdminGroup", timeout);

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
