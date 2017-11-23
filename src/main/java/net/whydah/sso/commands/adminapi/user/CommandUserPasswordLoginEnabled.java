package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommandForBooleanType;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.ddd.model.user.UserName;

import java.net.URI;

public class CommandUserPasswordLoginEnabled extends BaseHttpGetHystrixCommandForBooleanType {

    private String userName;


    public CommandUserPasswordLoginEnabled(URI userAdminServiceUri, String applicationTokenId, String userName) {
        super(userAdminServiceUri, "", applicationTokenId, "UASUserAdminGroup");
//        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup", 6000);

        if (userAdminServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserName.isValid(userName)) {
            log.error("CommandUserPasswordLoginEnabled initialized with null-values - will fail - userAdminServiceUri:{}, myAppTokenId:{}, userName:{}", userAdminServiceUri, applicationTokenId, userName);

        }
        this.userName = userName;

    }

    @Override
    protected Boolean dealWithResponse(String response) {
        return Boolean.valueOf(response);
    }


    @Override
    protected String getTargetPath() {
        return myAppTokenId + "/user/" + userName + "/password_login_enabled";
    }


}
