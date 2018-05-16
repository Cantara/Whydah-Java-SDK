package net.whydah.sso.commands.userauth;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommandForBooleanType;


//TODO:make test
public class CommandSendSmsPin extends BaseHttpPostHystrixCommandForBooleanType {

	public static int DEFAULT_TIMEOUT = 6000;
    private String phoneNo;
    private String pin;
    private String appTokenXml;

    public CommandSendSmsPin(URI tokenServiceUri, String appTokenId, String appTokenXml, String phoneNo, String pin) {
        super(tokenServiceUri, appTokenXml, appTokenId, "SSOAUserAuthGroup", DEFAULT_TIMEOUT);


        this.phoneNo = phoneNo;
        this.pin = pin;
        this.appTokenXml = appTokenXml;
        if (tokenServiceUri == null || appTokenXml == null || this.appTokenXml == null || this.phoneNo == null || this.pin == null) {
            log.error("{} initialized with null-values - will fail", CommandSendSmsPin.class.getSimpleName());
        }
    }
    
    public CommandSendSmsPin(URI tokenServiceUri, String appTokenId, String appTokenXml, String phoneNo, String pin, int timeout) {
        super(tokenServiceUri, appTokenXml, appTokenId, "SSOAUserAuthGroup", timeout);


        this.phoneNo = phoneNo;
        this.pin = pin;
        this.appTokenXml = appTokenXml;
        if (tokenServiceUri == null || appTokenXml == null || this.appTokenXml == null || this.phoneNo == null || this.pin == null) {
            log.error("{} initialized with null-values - will fail", CommandSendSmsPin.class.getSimpleName());
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
        data.put("smsPin", pin);
        return data;
    }


    // return myAppTokenId + "/" + adminUserTokenId + "/useraggregate" + "/" + userID;
    @Override
    protected String getTargetPath() {
        return "user/" + myAppTokenId + "/send_sms_pin";
    }
}
