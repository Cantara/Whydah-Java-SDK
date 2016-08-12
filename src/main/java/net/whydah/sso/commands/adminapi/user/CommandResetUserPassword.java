package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;

public class CommandResetUserPassword extends BaseHttpGetHystrixCommand<Boolean> {

    private String userName;
    private String emailTemplateName = "";


    public CommandResetUserPassword(URI userAdminServiceUri, String userName) {
        super(userAdminServiceUri, "", "", "UASUserActionGroup", 2000);

        this.userName = userName;
        if (userAdminServiceUri == null || userName == null) {
            log.error("CommandUserExists initialized with null-values - will fail - userAdminServiceUri:{}, userName:{}, adminUserTokenId:{}, userQuery:{}", userAdminServiceUri, userName);

        }

    }

    public CommandResetUserPassword(URI userAdminServiceUri, String userName, String emailTemplateName) {
        super(userAdminServiceUri, "", "", "UASUserActionGroup", 2000);

        this.userName = userName;
        this.emailTemplateName = emailTemplateName;
        if (userAdminServiceUri == null || userName == null) {
            log.error("CommandUserExists initialized with null-values - will fail - userAdminServiceUri:{}, userName:{}, adminUserTokenId:{}, userQuery:{}", userAdminServiceUri, userName);

        }

    }

    @Override
    protected Boolean dealWithResponse(String response) {
        return response.length() > 32;
    }


    @Override
    protected String getTargetPath() {

        if (emailTemplateName == null || emailTemplateName.length() < 3) {
            return myAppTokenId + "/auth/password/reset/username/" + userName;
        }
        return myAppTokenId + "/auth/password/reset/username/" + userName + "/" + emailTemplateName;
    }


}
