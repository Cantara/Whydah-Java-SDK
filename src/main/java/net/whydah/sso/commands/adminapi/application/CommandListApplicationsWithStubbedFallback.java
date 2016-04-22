package net.whydah.sso.commands.adminapi.application;


import net.whydah.sso.application.helpers.ApplicationHelper;

import java.net.URI;

public class CommandListApplicationsWithStubbedFallback extends CommandListApplications {

    public CommandListApplicationsWithStubbedFallback(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String applicationQuery) {
        super(userAdminServiceUri, myAppTokenId, adminUserTokenId, applicationQuery);
    }


    @Override
    protected String getFallback() {

        log.warn("CommandListApplicationsWithStubbedFallback - getFallback - override with fallback ");
        return ApplicationHelper.getDummyAppllicationListJson();
    }

}
