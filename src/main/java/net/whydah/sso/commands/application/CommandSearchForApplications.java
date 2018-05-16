package net.whydah.sso.commands.application;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;

import java.net.URI;

public class CommandSearchForApplications extends BaseHttpGetHystrixCommand<String> {


    private String applicationQuery;
    public static int DEFAULT_TIMEOUT = 6000;

    public CommandSearchForApplications(URI userAdminServiceUri, String applicationTokenId, String applicationQuery) {
        super(userAdminServiceUri, "", applicationTokenId, "ApplicationQueryGroup", DEFAULT_TIMEOUT);
        this.applicationQuery = applicationQuery;
        if (userAdminServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || applicationQuery == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }
    }

    public CommandSearchForApplications(URI userAdminServiceUri, String applicationTokenId, String applicationQuery, int timeout) {
        super(userAdminServiceUri, "", applicationTokenId, "ApplicationQueryGroup", timeout);
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
