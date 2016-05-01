package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


//TODO:make test
public class CommandSendSmsPin extends BaseHttpGetHystrixCommand<Boolean> {


    private String phoneNo;
    private String msg;
    private String appTokenXml;

    public CommandSendSmsPin(URI tokenServiceUri, String appTokenId, String appTokenXml, String phoneNo, String msg) {
        super(tokenServiceUri, appTokenXml, appTokenId, "SSOAUserAuthGroup");


        this.phoneNo = phoneNo;
        this.msg = msg;
        this.appTokenXml = appTokenXml;
        if (tokenServiceUri == null || appTokenXml == null || this.appTokenXml == null || this.phoneNo == null || this.msg == null) {
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
        data.put("apptoken", appTokenXml);
        data.put("phoneNo", phoneNo);
        data.put("smsPin", msg);
        return data;
    }


    // return myAppTokenId + "/" + adminUserTokenId + "/useraggregate" + "/" + userID;
    @Override
    protected String getTargetPath() {
        return myAppTokenId + "/send_sms_pin";
    }
}
