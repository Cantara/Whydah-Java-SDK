package net.whydah.sso.commands.adminapi.user;


import net.whydah.sso.user.helpers.UserHelper;
import org.slf4j.Logger;

import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandListUsersWithStubbedFallback extends CommandListUsers {
    private static final Logger log = getLogger(CommandListUsersWithStubbedFallback.class);


    public CommandListUsersWithStubbedFallback(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userQuery) {
        super(userAdminServiceUri, myAppTokenId, adminUserTokenId, userQuery);
    }

    @Override
    protected String getFallback() {

        log.warn("CommandListUsersWithStubbedFallback - getFallback - override with fallback ");

        return UserHelper.getDummyUserListJson();
    }




}
