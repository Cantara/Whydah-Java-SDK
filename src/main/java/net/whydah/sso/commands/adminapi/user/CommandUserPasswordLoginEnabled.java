package net.whydah.sso.commands.adminapi.user;

import java.net.URI;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommandForBooleanType;

public class CommandUserPasswordLoginEnabled extends BaseHttpGetHystrixCommandForBooleanType {

    private String userName;


    public CommandUserPasswordLoginEnabled(URI userAdminServiceUri, String myAppTokenId, String userName) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup");
//        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup", 6000);

        this.userName = userName;
        if (userAdminServiceUri == null || myAppTokenId == null || userName == null) {
            log.error("CommandUserPasswordLoginEnabled initialized with null-values - will fail - userAdminServiceUri:{}, myAppTokenId:{}, userName:{}", userAdminServiceUri, myAppTokenId, userName);

        }

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
