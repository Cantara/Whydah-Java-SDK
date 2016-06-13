package net.whydah.sso.commands.extras;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class CommandSendScheduledMail extends BaseHttpPostHystrixCommand<Boolean> {


    private String timestamp;
    private String emailaddress;
    private String subject;
    private String msg;

    public CommandSendScheduledMail(URI uasServiceUri, String appTokenId, String appTokenXml, String timestamp, String emailaddress, String subject, String msg) {
        super(uasServiceUri, appTokenXml, appTokenId, "SSOAUserAuthGroup");

        this.timestamp = timestamp;
        this.emailaddress = emailaddress;
        this.subject = subject;
        this.msg = msg;
        if (uasServiceUri == null || appTokenXml == null || this.emailaddress == null || this.timestamp == null || this.subject == null || this.msg == null) {
            log.error("{} initialized with null-values - will fail", CommandSendScheduledMail.class.getSimpleName());
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
        data.put("emailaddress", emailaddress);
        data.put("subject", subject);
        data.put("emailMessage", msg);
        return data;
    }


    @Override
    protected String getTargetPath() {
        return myAppTokenId + "/send_scheduled_email";
    }
}
