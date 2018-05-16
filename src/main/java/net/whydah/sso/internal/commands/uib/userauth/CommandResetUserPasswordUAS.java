package net.whydah.sso.internal.commands.uib.userauth;

import java.net.URI;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

public class CommandResetUserPasswordUAS extends BaseHttpPostHystrixCommand<String> {
    private String uid;
    public static int DEFAULT_TIMEOUT = 6000;
    
    public CommandResetUserPasswordUAS(String uibUri, String applicationtokenId, String uid) {
        super(URI.create(uibUri), "", applicationtokenId, "SSOAUserAuthGroup", DEFAULT_TIMEOUT);
        this.uid = uid;
        if (uibUri == null || applicationtokenId == null || uid == null) {
            this.log.error("{} initialized with null-values - will fail", CommandResetUserPasswordUAS.class.getSimpleName());
        }

    }
    
    public CommandResetUserPasswordUAS(String uibUri, String applicationtokenId, String uid, int timeout) {
        super(URI.create(uibUri), "", applicationtokenId, "SSOAUserAuthGroup", timeout);
        this.uid = uid;
        if (uibUri == null || applicationtokenId == null || uid == null) {
            this.log.error("{} initialized with null-values - will fail", CommandResetUserPasswordUAS.class.getSimpleName());
        }

    }

    protected String getTargetPath() {
        return this.myAppTokenId + "/user/" + this.uid + "/reset_password";
    }
}
