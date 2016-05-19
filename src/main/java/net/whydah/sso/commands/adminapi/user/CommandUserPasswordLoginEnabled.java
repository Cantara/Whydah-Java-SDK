package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;

public class CommandUserPasswordLoginEnabled extends BaseHttpGetHystrixCommand<Boolean> {

    private String userName;


    public CommandUserPasswordLoginEnabled(URI userAdminServiceUri, String myAppTokenId, String userName) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup");

        this.userName = userName;
        if (userAdminServiceUri == null || myAppTokenId == null || userName == null) {
            log.error("CommandUserExists initialized with null-values - will fail - userAdminServiceUri:{}, myAppTokenId:{}, userName:{}", userAdminServiceUri, myAppTokenId, userName);

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


    /**
     *     @Path("/user/{username}") public Response hasUserNameSetPassword(@PathParam("applicationtokenid") String applicationtokenid,
     @PathParam("username") String username) {

     */
}