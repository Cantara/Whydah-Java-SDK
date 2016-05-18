package net.whydah.sso.commands.extras;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class CommandSendScheduledSms extends BaseHttpPostHystrixCommand<Boolean> {


    private String timestamp;
    private String phoneNo;
    private String msg;

    public CommandSendScheduledSms(URI tokenServiceUri, String appTokenId, String appTokenXml, String timestamp, String phoneNo, String msg) {
        super(tokenServiceUri, appTokenXml, appTokenId, "SSOAUserAuthGroup");

        this.timestamp = timestamp;
        this.phoneNo = phoneNo;
        this.msg = msg;
        if (tokenServiceUri == null || appTokenXml == null || this.timestamp == null || this.phoneNo == null || this.msg == null) {
            log.error("{} initialized with null-values - will fail", CommandSendScheduledSms.class.getSimpleName());
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
        data.put("timestamp", timestamp);
        data.put("phoneNo", phoneNo);
        data.put("smsMessage", msg);
        return data;
    }


    @Override
    protected String getTargetPath() {
        return "user/" + myAppTokenId + "/send_scheduled_sms";
    }
}
