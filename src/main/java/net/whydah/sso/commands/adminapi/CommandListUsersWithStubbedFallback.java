package net.whydah.sso.commands.adminapi;

import net.whydah.sso.user.UserHelper;

import java.net.URI;

/**
 * Created by totto on 25.06.15.
 */
public class CommandListUsersWithStubbedFallback extends CommandListUsers {


    public CommandListUsersWithStubbedFallback(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userQuery) {
        super(userAdminServiceUri, myAppTokenId, adminUserTokenId, userQuery);
    }

    @Override
    protected String getFallback() {

        return UserHelper.getDummyUserListJson();
    }




}
