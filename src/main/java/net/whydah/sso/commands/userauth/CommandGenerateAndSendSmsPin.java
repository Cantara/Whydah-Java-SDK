package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommandForBooleanType;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class CommandGenerateAndSendSmsPin extends BaseHttpPostHystrixCommandForBooleanType {

	public static int DEFAULT_TIMEOUT = 6000;
    private String phoneNo;

    @Deprecated
    public CommandGenerateAndSendSmsPin(URI tokenServiceUri, String applicationTokenId, String appTokenXml, String phoneNo) {
        super(tokenServiceUri, appTokenXml, applicationTokenId, "SSOAUserAuthGroup", DEFAULT_TIMEOUT);

        if (tokenServiceUri == null || appTokenXml == null || !ApplicationTokenID.isValid(applicationTokenId) || phoneNo == null) {
            log.error("{} initialized with null-values - will fail", CommandGenerateAndSendSmsPin.class.getSimpleName());
        }

        this.phoneNo = phoneNo;
    }
    
    public CommandGenerateAndSendSmsPin(URI tokenServiceUri, String applicationTokenId, String phoneNo) {
        super(tokenServiceUri, null, applicationTokenId, "SSOAUserAuthGroup", DEFAULT_TIMEOUT);

        if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || phoneNo == null) {
            log.error("{} initialized with null-values - will fail", CommandGenerateAndSendSmsPin.class.getSimpleName());
        }

        this.phoneNo = phoneNo;
    }

    public CommandGenerateAndSendSmsPin(URI tokenServiceUri, String applicationTokenId, String phoneNo, int timeout) {
        super(tokenServiceUri, null, applicationTokenId, "SSOAUserAuthGroup", timeout);

        if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || phoneNo == null) {
            log.error("{} initialized with null-values - will fail", CommandGenerateAndSendSmsPin.class.getSimpleName());
        }

        this.phoneNo = phoneNo;
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
        return data;
    }


    // return myAppTokenId + "/" + adminUserTokenId + "/useraggregate" + "/" + userID;
    @Override
    protected String getTargetPath() {
        return "user/" + myAppTokenId + "/generate_pin_and_send_sms_pin";
    }
}
