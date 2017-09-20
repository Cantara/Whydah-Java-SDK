package net.whydah.sso.internal.commands.uib.userauth;

import java.net.URI;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

public class CommandResetUserPasswordUAS extends BaseHttpPostHystrixCommand<String> {
    private String uid;

    public CommandResetUserPasswordUAS(String uibUri, String applicationtokenId, String uid) {
        super(URI.create(uibUri), "", applicationtokenId, "SSOAUserAuthGroup");
        this.uid = uid;
        if (uibUri == null || applicationtokenId == null || uid == null) {
            this.log.error("{} initialized with null-values - will fail", CommandResetUserPasswordUAS.class.getSimpleName());
        }

    }

    protected String getTargetPath() {
        return this.myAppTokenId + "/user/" + this.uid + "/reset_password";
    }
}
