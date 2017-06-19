package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.user.mappers.UserCredentialMapper;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.ExceptionUtil;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandLogonUserByUserCredential  extends BaseHttpPostHystrixCommand<String> {

    private UserCredential userCredential;
    private String userticket;


    public CommandLogonUserByUserCredential(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, UserCredential userCredential) {
        super(tokenServiceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", 6000);
        this.userCredential=userCredential;
        this.userticket= UUID.randomUUID().toString();  // Create new UUID ticket if not provided
        if (tokenServiceUri == null || myAppTokenId == null || userCredential == null) {
            log.error("CommandLogonUserByUserCredential initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, myAppTokenXml:{}, userCredential:*****", tokenServiceUri, myAppTokenId, myAppTokenXml, userCredential);
        }
    }

    public CommandLogonUserByUserCredential(URI tokenServiceUri,String myAppTokenId,String myAppTokenXml ,UserCredential userCredential,String userticket) {
        this(tokenServiceUri,myAppTokenId,myAppTokenXml,userCredential);
        this.userticket=userticket;
    }

    public CommandLogonUserByUserCredential(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, String userCredentialXml, String userticket) {
        this(tokenServiceUri, myAppTokenId, myAppTokenXml, UserCredentialMapper.fromXml(userCredentialXml));
        this.userticket = userticket;
    }


	@Override
	protected String getTargetPath() {
		return "user/" + myAppTokenId + "/" + userticket + "/usertoken";
	}


	@Override
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("apptoken", myAppTokenXml);
		data.put("usercredential", UserCredentialMapper.toXML(userCredential));
		return data;
	}

	int retryCnt=0;
	@Override
	protected String dealWithFailedResponse(String responseBody, int statusCode) {
		if(statusCode != java.net.HttpURLConnection.HTTP_FORBIDDEN &&retryCnt<1){
			//do retry
			retryCnt++;
			return doPostCommand();
		} else {
			String authenticationFailedMessage = ExceptionUtil.printableUrlErrorMessage("User session failed", request, statusCode);
    		log.warn(authenticationFailedMessage);
			return null;
		}
	}

//
//	 @Override
//	    protected String run() {
//	        log.trace("CommandLogonUserByUserCredential - uri={} myAppTokenId={}", tokenServiceUri.toString(), myAppTokenId);
//
//	        Client tokenServiceClient = ClientBuilder.newClient();
//	        WebTarget getUserToken = tokenServiceClient.target(tokenServiceUri).path("user/" + myAppTokenId + "/" + userticket + "/usertoken");
//	        Form formData = new Form();
//	        formData.param("apptoken", myAppTokenXml);
//	        formData.param("usercredential", UserCredentialMapper.toXML(userCredential));
//	        Response response = postForm(formData,getUserToken);
//	        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
//	            log.warn("CommandLogonUserByUserCredential - getUserToken - User authentication failed with status code " + response.getStatus());
//	            return null;
//	        }
//	        if (response.getStatus() == OK.getStatusCode()) {
//	            String responseXML = response.readEntity(String.class);
//	            log.trace("CommandLogonUserByUserCredential - getUserToken - Log on OK with response {}", responseXML);
//	            return responseXML;
//	        }
//
//	        //retry once for other statuses
//	        log.info("CommandLogonUserByUserCredential - getUserToken - retry once for other statuses");
//	        response = postForm(formData,getUserToken);
//	        if (response.getStatus() == OK.getStatusCode()) {
//	            String responseXML = response.readEntity(String.class);
//	            log.trace("CommandLogonUserByUserCredential - getUserToken - Log on OK with response {}", responseXML);
//	            return responseXML;
//	        } else if (response.getStatus() == NOT_FOUND.getStatusCode()) {
//	            log.error(ExceptionUtil.printableUrlErrorMessage("CommandLogonUserByUserCredential - getUserToken - Auth failed - Problems connecting with TokenService", getUserToken, response));
//	        } else {
//	            log.info(ExceptionUtil.printableUrlErrorMessage("CommandLogonUserByUserCredential - getUserToken - User authentication failed", getUserToken, response));
//	        }
//	        return null;
//
//	    }
//
//	    private Response postForm(Form formData, WebTarget logonResource) {
//	        return logonResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
//	    }
//
//	    @Override
//	    protected String getFallback() {
//	        log.warn("CommandLogonUserByUserCredential - fallback - uri={}", tokenServiceUri.toString());
//	        return null;
//	    }


}