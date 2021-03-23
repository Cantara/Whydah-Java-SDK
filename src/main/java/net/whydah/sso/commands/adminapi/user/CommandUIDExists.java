package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommandForBooleanType;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.ddd.model.user.UserTokenId;

import java.net.URI;

public class CommandUIDExists extends BaseHttpGetHystrixCommandForBooleanType {

    public static int DEFAULT_TIMEOUT = 6000;

    private String adminUserTokenId;
    private String uID;


    public CommandUIDExists(URI userAdminServiceUri, String applicationTokenId, String adminUserTokenId, String uID) {
        super(userAdminServiceUri, "", applicationTokenId, "UASUserAdminGroup", DEFAULT_TIMEOUT);

        if (userAdminServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(adminUserTokenId) || uID == null) {
            log.error("CommandUserExists initialized with null-values - will fail - userAdminServiceUri:{}, myAppTokenId:{}, adminUserTokenId:{}, userQuery:{}", userAdminServiceUri, applicationTokenId, adminUserTokenId, uID);

        }
        this.adminUserTokenId = adminUserTokenId;
        this.uID = uID;

    }


    public CommandUIDExists(URI userAdminServiceUri, String applicationTokenId, String adminUserTokenId, String userQuery, int timeout) {
        super(userAdminServiceUri, "", applicationTokenId, "UASUserAdminGroup", timeout);

        if (userAdminServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(adminUserTokenId) || userQuery == null) {
            log.error("CommandUserExists initialized with null-values - will fail - userAdminServiceUri:{}, myAppTokenId:{}, adminUserTokenId:{}, userQuery:{}", userAdminServiceUri, applicationTokenId, adminUserTokenId, userQuery);

        }
        this.adminUserTokenId = adminUserTokenId;
        this.uID = userQuery;

    }

    @Override
    protected Boolean dealWithResponse(String response) {
        if (response != null && response.contains("\"uid\":\"" + uID + "\"")) {
            return true;
        }
        return false;
    }


    @Override
    protected String getTargetPath() {
        return myAppTokenId + "/" + adminUserTokenId + "/users" + "/find/" + uID;
    }


}
