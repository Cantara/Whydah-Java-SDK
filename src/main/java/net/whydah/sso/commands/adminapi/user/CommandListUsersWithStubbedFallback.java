package net.whydah.sso.commands.adminapi.user;


import java.net.URI;

import net.whydah.sso.user.helpers.UserHelper;

public class CommandListUsersWithStubbedFallback extends CommandListUsers {
   
    public CommandListUsersWithStubbedFallback(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userQuery) {
        super(userAdminServiceUri, myAppTokenId, adminUserTokenId, userQuery);
    }

    @Override
    protected String getFallback() {

        log.warn("CommandListUsersWithStubbedFallback - getFallback - override with fallback ");

        return UserHelper.getDummyUserListJson();
    }




}
