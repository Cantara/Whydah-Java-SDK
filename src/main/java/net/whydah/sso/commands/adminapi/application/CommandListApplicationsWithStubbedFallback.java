package net.whydah.sso.commands.adminapi.application;


import net.whydah.sso.application.helpers.ApplicationHelper;
import org.slf4j.Logger;

import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandListApplicationsWithStubbedFallback extends CommandListApplications {

    private static final Logger log = getLogger(CommandListApplicationsWithStubbedFallback.class);


    public CommandListApplicationsWithStubbedFallback(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String applicationQuery) {
        super(userAdminServiceUri, myAppTokenId, adminUserTokenId, applicationQuery);
    }

    @Override
    protected String getFallback() {

        log.warn("CommandListApplicationsWithStubbedFallback - getFallback - override with fallback ");
        return ApplicationHelper.getDummyAppllicationListJson();
    }


}
