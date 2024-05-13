package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommandForBooleanType;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;

import java.net.URI;

public class CommandUserThirdPartyLoginEnabled extends BaseHttpGetHystrixCommandForBooleanType {

    private String userName;
    private String provider;
    public static int DEFAULT_TIMEOUT = 6000;

    public CommandUserThirdPartyLoginEnabled(URI userAdminServiceUri, String myAppTokenId, String userName, String provider) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup", DEFAULT_TIMEOUT);
//        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup", 6000);

        this.userName = userName;
        this.provider = provider;
        if (userAdminServiceUri == null || !ApplicationTokenID.isValid(myAppTokenId) || userName == null || provider == null) {
            log.error("CommandUserPasswordLoginEnabled initialized with null-values - will fail - userAdminServiceUri:{}, myAppTokenId:{}, userName:{}", userAdminServiceUri, myAppTokenId, userName);

        }

    }

    public CommandUserThirdPartyLoginEnabled(URI userAdminServiceUri, String myAppTokenId, String userName, String provider, int timeout) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup", timeout);
//        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup", 6000);

        this.userName = userName;
        this.provider = provider;
        if (userAdminServiceUri == null || !ApplicationTokenID.isValid(myAppTokenId) || userName == null || provider == null) {
            log.error("CommandUserPasswordLoginEnabled initialized with null-values - will fail - userAdminServiceUri:{}, myAppTokenId:{}, userName:{}", userAdminServiceUri, myAppTokenId, userName);

        }

    }
    
    @Override
    protected Boolean dealWithResponse(String response) {
        return Boolean.valueOf(response);
    }


    @Override
    protected String getTargetPath() {
        return myAppTokenId + "/user/" + userName + "/" + provider + "/thirdparty_login_enabled";
    }


}
