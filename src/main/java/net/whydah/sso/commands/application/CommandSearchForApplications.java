package net.whydah.sso.commands.application;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;

import java.net.URI;

public class CommandSearchForApplications extends BaseHttpGetHystrixCommand<String> {


    private String applicationQuery;

    public CommandSearchForApplications(URI userAdminServiceUri, String applicationTokenId, String applicationQuery) {
        super(userAdminServiceUri, "", applicationTokenId, "ApplicationQueryGroup", 6000);
        this.applicationQuery = applicationQuery;
        if (userAdminServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || applicationQuery == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }
    }


    @Override
    protected String getTargetPath() {
        return myAppTokenId + "/find/applications/" + applicationQuery;
    }


}
