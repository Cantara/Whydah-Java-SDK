package net.whydah.sso.commands.adminapi.application;


import java.net.URI;

public class CommandListApplicationsWithStubbedFallback extends CommandListApplications {

    public CommandListApplicationsWithStubbedFallback(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String applicationQuery) {
        super(userAdminServiceUri, myAppTokenId, adminUserTokenId, applicationQuery);
    }

}
