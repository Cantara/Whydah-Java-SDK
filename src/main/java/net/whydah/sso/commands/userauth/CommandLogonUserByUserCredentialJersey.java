package net.whydah.sso.commands.userauth;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.user.mappers.UserCredentialMapper;
import net.whydah.sso.user.types.UserCredential;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLogonUserByUserCredentialJersey  extends BaseHttpPostHystrixCommand<String> {

    private UserCredential userCredential;
    private String userticket;


    public CommandLogonUserByUserCredentialJersey(URI tokenServiceUri,String myAppTokenId,String myAppTokenXml ,UserCredential userCredential) {
    	super(tokenServiceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", 3000);
        this.userCredential=userCredential;
        this.userticket= UUID.randomUUID().toString();  // Create new UUID ticket if not provided
        if (tokenServiceUri == null || myAppTokenId == null || myAppTokenXml == null || userCredential == null) {
            log.error("CommandLogonUserByUserCredential initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, myAppTokenXml:{}, userCredential:*****", tokenServiceUri, myAppTokenId, myAppTokenXml, userCredential);
        }
    }


    public CommandLogonUserByUserCredentialJersey(URI tokenServiceUri,String myAppTokenId,String myAppTokenXml ,UserCredential userCredential,String userticket) {
        this(tokenServiceUri,myAppTokenId,myAppTokenXml,userCredential);
        this.userticket=userticket;
    }

    public CommandLogonUserByUserCredentialJersey(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, String userCredentialXml, String userticket) {
        this(tokenServiceUri, myAppTokenId, myAppTokenXml, UserCredentialMapper.fromXml(userCredentialXml));
        this.userticket = userticket;
    }

//    @Override
//    protected String run() {
//        log.trace("CommandLogonUserByUserCredential - whydahServiceUri={} myAppTokenId={}", tokenServiceUri.toString(), myAppTokenId);
//
//        Client tokenServiceClient = ClientBuilder.newClient();
//        WebTarget getUserToken = tokenServiceClient.target(tokenServiceUri).path("user/" + myAppTokenId + "/" + userticket + "/usertoken");
//        Form formData = new Form();
//        formData.param("apptoken", myAppTokenXml);
//        formData.param("usercredential", UserCredentialMapper.toXML(userCredential));
//        Response response = postForm(formData,getUserToken);
//        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
//            log.warn("CommandLogonUserByUserCredential - getUserToken - User authentication failed with status code " + response.getStatus());
//            return null;
//        }
//        if (response.getStatus() == OK.getStatusCode()) {
//            String responseXML = response.readEntity(String.class);
//            log.trace("CommandLogonUserByUserCredential - getUserToken - Log on OK with response {}", responseXML);
//            return responseXML;
//        }
//
//        //retry once for other statuses
//        log.info("CommandLogonUserByUserCredential - getUserToken - retry once for other statuses");
//        response = postForm(formData,getUserToken);
//        if (response.getStatus() == OK.getStatusCode()) {
//            String responseXML = response.readEntity(String.class);
//            log.trace("CommandLogonUserByUserCredential - getUserToken - Log on OK with response {}", responseXML);
//            return responseXML;
//        } else if (response.getStatus() == NOT_FOUND.getStatusCode()) {
//            log.error(ExceptionUtil.printableUrlErrorMessage("CommandLogonUserByUserCredential - getUserToken - Auth failed - Problems connecting with TokenService", getUserToken, response));
//        } else {
//            log.info(ExceptionUtil.printableUrlErrorMessage("CommandLogonUserByUserCredential - getUserToken - User authentication failed", getUserToken, response));
//        }
//        return null;
//
//    }
//
//    private Response postForm(Form formData, WebTarget logonResource) {
//        return logonResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
//    }
//
//    @Override
//    protected String getFallback() {
//        log.warn("CommandLogonUserByUserCredential - fallback - whydahServiceUri={}", tokenServiceUri.toString());
//        return null;
//    }


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


}