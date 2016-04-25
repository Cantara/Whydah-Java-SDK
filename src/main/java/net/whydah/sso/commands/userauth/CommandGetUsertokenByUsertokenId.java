package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.util.ExceptionUtil;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;


public class CommandGetUsertokenByUsertokenId extends BaseHttpPostHystrixCommand<String> {

   // private static final Logger log = LoggerFactory.getLogger(CommandGetUsertokenByUsertokenId.class);

    private String usertokenId;
    


    public CommandGetUsertokenByUsertokenId(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, String usertokenId) {
    	super(tokenServiceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", 6000);
        
        this.usertokenId = usertokenId;
        
        if (tokenServiceUri == null || myAppTokenId == null || myAppTokenXml == null || usertokenId == null) {
			log.error("CommandGetUsertokenByUsertokenId initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, myAppTokenXml:{}  usertokenId:{}", tokenServiceUri.toString(), myAppTokenId, myAppTokenXml, usertokenId);
		}

    }

//    @Override
//    protected String run() {
//
//        String responseXML = null;
//        log.trace("CommandGetUsertokenByUsertokenId - whydahServiceUri={} myAppTokenId={}, usertokenId:{}", tokenServiceUri.toString(), myAppTokenId, usertokenId);
//
//
//        Client tokenServiceClient = ClientBuilder.newClient();
//
//        WebTarget userTokenResource = tokenServiceClient.target(tokenServiceUri).path("user/" + myAppTokenId + "/get_usertoken_by_usertokenid");
//        log.trace("CommandGetUsertokenByUsertokenId  - usertokenid: {} apptoken: {}", usertokenId, myAppTokenXml);
//        Form formData = new Form();
//        formData.param("apptoken", myAppTokenXml);  //
//        formData.param("usertokenid", usertokenId);  //usertokenid
//
//        Response response = userTokenResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
//        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
//            log.debug("CommandGetUsertokenByUsertokenId - Response Code from STS: {}", response.getStatus());
//            throw new IllegalArgumentException("CommandGetUsertokenByUsertokenId failed.");
//        }
//        if (!(response.getStatus() == OK.getStatusCode())) {
//            log.debug("CommandGetUsertokenByUsertokenId - Response Code from STS: {}", response.getStatus());
//        }
//        responseXML = response.readEntity(String.class);
//        log.debug("CommandGetUsertokenByUsertokenId - Response OK with XML: {}", responseXML);
//
//        if (responseXML == null) {
//            String authenticationFailedMessage = ExceptionUtil.printableUrlErrorMessage("User session failed", userTokenResource, response);
//            log.warn(authenticationFailedMessage);
//            throw new RuntimeException(authenticationFailedMessage);
//        }
//
//        return responseXML;
//
//
//    }
    
    
    @Override
    protected String dealWithFailedResponse(String responseBody, int statusCode) {
    	if (statusCode == FORBIDDEN.getStatusCode()) {
            log.debug("CommandGetUsertokenByUsertokenId - Response Code from STS: {}", statusCode);
            throw new IllegalArgumentException("CommandGetUsertokenByUsertokenId failed.");
    	} else {
    		String authenticationFailedMessage = ExceptionUtil.printableUrlErrorMessage("User session failed", request, statusCode);
    		log.warn(authenticationFailedMessage);
    		throw new RuntimeException(authenticationFailedMessage);
        }
    }

//    @Override
//    protected String getFallback() {
//        log.warn("CommandGetUsertokenByUsertokenId - fallback - whydahServiceUri={} - usertokenId:{} - myAppTokenId: {}", tokenServiceUri.toString(), usertokenId, myAppTokenId);
//        return null;
//    }
    
    @Override
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("apptoken", myAppTokenXml);
		data.put("usertokenid", usertokenId);
		return data;
	}
    
	@Override
	protected String getTargetPath() {

		return "user/" + myAppTokenId + "/get_usertoken_by_usertokenid";
	}


}