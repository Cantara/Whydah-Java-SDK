package net.whydah.sso.commands.application;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;

public class CommandSearchForApplications extends BaseHttpGetHystrixCommand<String> {


    private String applicationQuery;

    public CommandSearchForApplications(URI userAdminServiceUri, String myAppTokenId, String applicationQuery) {
        super(userAdminServiceUri, "", myAppTokenId, "ApplicationQueryGroup", 6000);
        this.applicationQuery = applicationQuery;
        if (userAdminServiceUri == null || myAppTokenId == null || applicationQuery == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }
    }


    @Override
    protected String getTargetPath() {
        return myAppTokenId + "/find/applications/" + applicationQuery;
    }


}
