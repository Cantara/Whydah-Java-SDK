package net.whydah.sso.commands.appauth;


import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.util.HttpSender;

import java.net.URI;

public class CommandRenewApplicationSession extends BaseHttpPostHystrixCommand<String> {

  

    public CommandRenewApplicationSession(URI tokenServiceUri, String applicationtokenid) {
    	super(tokenServiceUri, "", applicationtokenid, "STSApplicationAdminGroup", 3000);
        

        if (tokenServiceUri == null || applicationtokenid == null) {
            log.error("CommandRenewApplicationSession initialized with null-values - will fail");
            throw new IllegalArgumentException("Missing parameters for \n" +
                    "\ttokenServiceUri [" + tokenServiceUri + "], \n" +
                    "\tapplicationtokenid [" + applicationtokenid + "]");
        }
      
    }

    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
        return request.contentType(HttpSender.APPLICATION_FORM_URLENCODED);
    }

//    @Override
//    protected String run() {
//        log.trace("CommandRenewApplicationSession - whydahServiceUri={} applicationtokenid={}", tokenServiceUri.toString(), applicationtokenid);
//
//        Client tokenServiceClient = ClientBuilder.newClient();
//        Form formData = new Form();
//
//        Response response;
//        WebTarget applicationRenewResource = tokenServiceClient.target(tokenServiceUri).path(applicationtokenid).path("renew_applicationtoken");
//        try {
//            response = postForm(formData, applicationRenewResource);
//        } catch (RuntimeException e) {
//            log.error("CommandRenewApplicationSession - renew_applicationtoken - Problem connecting to {}", applicationRenewResource.toString());
//            throw (e);
//        }
//        String myAppTokenXml = response.readEntity(String.class);
//        log.trace("CommandRenewApplicationSession - Applogon ok: apptokenxml: {}", myAppTokenXml);
//        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
//        log.debug("CommandRenewApplicationSession - myAppTokenId: {}", myApplicationTokenID);
//        return myAppTokenXml;
//    }
//
//    private Response postForm(Form formData, WebTarget applicationRenewResource) {
//        return applicationRenewResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
//    }

//    @Override
//    protected String getFallback() {
//        log.warn("CommandRenewApplicationSession - fallback - whydahServiceUri={}", tokenServiceUri.toString());
//        return null;
//    }


	@Override
	protected String getTargetPath() {
		return myAppTokenId+ "/renew_applicationtoken";
	}

}