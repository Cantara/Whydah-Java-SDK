package net.whydah.sso.commands.userauth;

import static org.slf4j.LoggerFactory.getLogger;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import org.slf4j.Logger;

public class CommandSendSmsPin extends BaseHttpPostHystrixCommand<Response> {
    
    
    private String phoneNo;
    private String pin;

    public CommandSendSmsPin(URI tokenServiceUri, String appTokenXml, String phoneNo, String pin) {
    	super(tokenServiceUri, appTokenXml, "", "SSOAUserAuthGroup");
        
        
        this.phoneNo = phoneNo;
        this.pin = pin;
        if (tokenServiceUri == null || appTokenXml == null || this.phoneNo == null || this.pin == null) {
            log.error("{} initialized with null-values - will fail", CommandSendSmsPin.class.getSimpleName());
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
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("phoneNo", phoneNo);
		data.put("smsPin", pin);
		return data;
	}


	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/send_sms_pin";
	}
}
