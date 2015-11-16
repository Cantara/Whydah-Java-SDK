package net.whydah.sso.commands.adminapi.user;


import net.whydah.sso.user.helpers.UserHelper;

import java.net.URI;

public class CommandListUsersWithStubbedFallback extends CommandListUsers {


    public CommandListUsersWithStubbedFallback(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userQuery) {
        super(userAdminServiceUri, myAppTokenId, adminUserTokenId, userQuery);
    }

    @Override
    protected String getFallback() {

        return UserHelper.getDummyUserListJson();
    }




}
