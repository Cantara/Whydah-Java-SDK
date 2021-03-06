package net.whydah.sso.commands.extras;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommandForBooleanType;

public class CommandSendSms extends BaseHttpPostHystrixCommandForBooleanType {
    
	public static int DEFAULT_TIMEOUT = 6000;
    private String phoneNo;
    private String msg;

    public CommandSendSms(URI tokenServiceUri, String appTokenId, String appTokenXml, String phoneNo, String msg) {
    	super(tokenServiceUri, appTokenXml, appTokenId, "SSOAUserAuthGroup", DEFAULT_TIMEOUT);
        
        
        this.phoneNo = phoneNo;
        this.msg = msg;
        if (tokenServiceUri == null || appTokenXml == null || this.phoneNo == null || this.msg == null) {
            log.error("{} initialized with null-values - will fail", CommandSendSms.class.getSimpleName());
        }
    }

    public CommandSendSms(URI tokenServiceUri, String appTokenId, String appTokenXml, String phoneNo, String msg, int timeout) {
    	super(tokenServiceUri, appTokenXml, appTokenId, "SSOAUserAuthGroup", timeout);
        
        
        this.phoneNo = phoneNo;
        this.msg = msg;
        if (tokenServiceUri == null || appTokenXml == null || this.phoneNo == null || this.msg == null) {
            log.error("{} initialized with null-values - will fail", CommandSendSms.class.getSimpleName());
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
        data.put("smsMessage", msg);
        return data;
	}


	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/send_sms";
	}
}
