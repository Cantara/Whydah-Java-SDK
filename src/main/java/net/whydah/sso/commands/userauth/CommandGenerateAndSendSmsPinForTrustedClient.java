package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommandForBooleanType;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class CommandGenerateAndSendSmsPinForTrustedClient extends BaseHttpPostHystrixCommandForBooleanType {

	public static int DEFAULT_TIMEOUT = 6000;
    private String phoneNo;
    private String msg;
    private String clientId;
    
    public CommandGenerateAndSendSmsPinForTrustedClient(URI tokenServiceUri, String applicationTokenId, String phoneNo, String clientId, String msg) {
        super(tokenServiceUri, null, applicationTokenId, "SSOAUserAuthGroup", DEFAULT_TIMEOUT);

        if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || phoneNo == null) {
            log.error("{} initialized with null-values - will fail", CommandGenerateAndSendSmsPinForTrustedClient.class.getSimpleName());
        }

        this.phoneNo = phoneNo;
        this.msg = msg;
        this.clientId = clientId;
    }

    public CommandGenerateAndSendSmsPinForTrustedClient(URI tokenServiceUri, String applicationTokenId, String phoneNo, String clientId, String msg, int timeout) {
        super(tokenServiceUri, null, applicationTokenId, "SSOAUserAuthGroup", timeout);

        if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || phoneNo == null) {
            log.error("{} initialized with null-values - will fail", CommandGenerateAndSendSmsPinForTrustedClient.class.getSimpleName());
        }

        this.phoneNo = phoneNo;
        this.msg = msg;
        this.clientId = clientId;
    }
    
    @Override
    protected Boolean dealWithFailedResponse(String responseBody, int statusCode) {
        return false;
    }

    @Override
    protected Boolean dealWithResponse(String response) {
        return true;
    }

    @Override
    protected Map<String, String> getFormParameters() {
        Map<String, String> data = new HashMap<String, String>();
        data.put("phoneNo", phoneNo);
        data.put("clientId", clientId);
        data.put("msg", msg);
        return data;
    }

    @Override
    protected String getTargetPath() {
        return "user/" + myAppTokenId + "/send_sms_pin_for_trusted_client";
    }
}
