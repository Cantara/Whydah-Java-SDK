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


    private String phoneNo;
    private String appTokenXml;

    public CommandGenerateAndSendSmsPin(URI tokenServiceUri, String applicationTokenId, String appTokenXml, String phoneNo) {
        super(tokenServiceUri, appTokenXml, applicationTokenId, "SSOAUserAuthGroup");

        if (tokenServiceUri == null || appTokenXml == null || this.appTokenXml == null || !ApplicationTokenID.isValid(applicationTokenId) || this.phoneNo == null) {
            log.error("{} initialized with null-values - will fail", CommandGenerateAndSendSmsPin.class.getSimpleName());
        }

        this.phoneNo = phoneNo;
        this.appTokenXml = appTokenXml;
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
