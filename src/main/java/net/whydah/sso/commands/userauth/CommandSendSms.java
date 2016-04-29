package net.whydah.sso.commands.userauth;

import static org.slf4j.LoggerFactory.getLogger;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import org.slf4j.Logger;

public class CommandSendSms extends BaseHttpPostHystrixCommand<Boolean> {
    
    
    private String phoneNo;
    private String msg;

    public CommandSendSms(URI tokenServiceUri, String appTokenId, String appTokenXml, String phoneNo, String msg) {
    	super(tokenServiceUri, appTokenXml, appTokenId, "SSOAUserAuthGroup");
        
        
        this.phoneNo = phoneNo;
        this.msg = msg;
        if (tokenServiceUri == null || appTokenXml == null || this.phoneNo == null || this.msg == null) {
            log.error("{} initialized with null-values - will fail", CommandSendSms.class.getSimpleName());
        }
    }

//    @Override
//    protected Response run() {
//        log.trace("{} - appTokenXml={}, ", CommandSendSmsPin.class.getSimpleName(), appTokenXml);
//
//        String myAppTokenId = UserTokenXpathHelper.getAppTokenIdFromAppToken(appTokenXml);
//
//        Client client = ClientBuilder.newClient();
//        WebTarget sts = client.target(tokenServiceUri);
//
//        WebTarget webResource = sts.path(myAppTokenId).path("send_sms_pin");
//        Form formData = new Form();
//        formData.param("phoneNo", phoneNo);
//        formData.param("smsPin", pin);
//
//        return webResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
//    }
//
//    @Override
//    protected Response getFallback() {
//        log.warn("{} - fallback - uibUri={}", CommandSendSmsPin.class.getSimpleName(), tokenServiceUri);
//        return null;
//    }
    
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
		data.put("smsPin", msg);
		return data;
	}


	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/send_sms";
	}
}
