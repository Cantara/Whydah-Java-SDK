package net.whydah.sso.commands.threat;


import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


public class CommandSendThreatSignal extends BaseHttpPostHystrixCommand<String> {

    private static final Logger log = LoggerFactory.getLogger(CommandSendThreatSignal.class);

    private URI tokenServiceUri;
    private String myAppTokenId;
    private String threatMessage;


    public CommandSendThreatSignal(URI tokenServiceUri, String myAppTokenId, String threatMessage) {
    	super(tokenServiceUri, "", myAppTokenId,"WhydahThreat",1000);
        
        
        this.threatMessage = threatMessage;
        if (tokenServiceUri == null || myAppTokenId == null) {
            log.error(TAG + " initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}", tokenServiceUri.toString(), myAppTokenId);
        }
    }

//    @Override
//    protected String run() {
//        log.trace("CommandSendThreatSignal - whydahServiceUri={} myAppTokenId={},", tokenServiceUri.toString(), myAppTokenId);
//
//        Client tokenServiceClient = ClientBuilder.newClient();
//        WebTarget userTokenResource = tokenServiceClient.target(tokenServiceUri).path("threat").path(myAppTokenId).path("signal");
//        log.trace("CommandSendThreatSignal  -  apptoken: {}", myAppTokenId);
//        Form formData = new Form();
//        formData.param("signal", threatMessage);
//        Response response = userTokenResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
//        if (!(response.getStatus() == OK.getStatusCode())) {
//            log.debug("CommandSendThreatSignal - Response Code from STS: {}", response.getStatus());
//        }
//        return "";
//
//
//    }
    @Override
    protected String dealWithResponse(String response) {
    	return "";
    }
    
    @Override
    protected String dealWithFailedResponse(String responseBody, int statusCode) {
    	return "";
    }

	@Override
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("signal", threatMessage);
		return data;
	}
    
//    @Override
//    protected String getFallback() {
//        log.warn("CommandSendThreatSignal - fallback - whydahServiceUri={} -  myAppTokenId: {}", tokenServiceUri.toString(), myAppTokenId);
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		return "threat/" + myAppTokenId + "/signal";
	}


}

