package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class CommandGenerateAndSendSmsPin extends BaseHttpPostHystrixCommand<Boolean> {


    private String phoneNo;
    private String pin;
    private String appTokenXml;

    public CommandGenerateAndSendSmsPin(URI tokenServiceUri, String appTokenId, String appTokenXml, String phoneNo) {
//        super(tokenServiceUri, appTokenXml, appTokenId, "SSOAUserAuthGroup", 6000);//should have timeout if you don't want to see the fallback
        super(tokenServiceUri, appTokenXml, appTokenId, "SSOAUserAuthGroup");


        this.phoneNo = phoneNo;
        this.pin = pin;
        this.appTokenXml = appTokenXml;
        if (tokenServiceUri == null || appTokenXml == null || this.appTokenXml == null || this.phoneNo == null) {
            log.error("{} initialized with null-values - will fail", CommandGenerateAndSendSmsPin.class.getSimpleName());
        }
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
