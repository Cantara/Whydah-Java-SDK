package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommandForBooleanType;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.ddd.model.user.UserTokenId;

import java.net.URI;

public class CommandUserExists extends BaseHttpGetHystrixCommandForBooleanType {
  
    private String adminUserTokenId;
    private String userQuery;


    public CommandUserExists(URI userAdminServiceUri, String applicationTokenId, String adminUserTokenId, String userQuery) {
        super(userAdminServiceUri, "", applicationTokenId, "UASUserAdminGroup", 2000);

        if (userAdminServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(adminUserTokenId) || userQuery == null) {
            log.error("CommandUserExists initialized with null-values - will fail - userAdminServiceUri:{}, myAppTokenId:{}, adminUserTokenId:{}, userQuery:{}", userAdminServiceUri, applicationTokenId, adminUserTokenId, userQuery);

        }
        this.adminUserTokenId = adminUserTokenId;
        this.userQuery = userQuery;

    }
    
    @Override
    protected Boolean dealWithResponse(String response) {
    	return response.length() > 32;
    }


	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + adminUserTokenId + "/users" + "/find/" + userQuery;
	}


}
