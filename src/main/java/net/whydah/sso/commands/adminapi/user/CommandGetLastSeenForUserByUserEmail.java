package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;


public class CommandGetLastSeenForUserByUserEmail extends BaseHttpGetHystrixCommand<String> {

    private String userEmail;


    public CommandGetLastSeenForUserByUserEmail(URI tokenServiceUri, String myAppTokenId, String userEmail) {
    	super(tokenServiceUri, "", myAppTokenId, "STSUserQueries", 6000);
        
        this.userEmail = userEmail;
        if (tokenServiceUri == null || myAppTokenId == null || userEmail == null) {
            log.error(TAG + " initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, userEmail:{}", tokenServiceUri.toString(), myAppTokenId, userEmail);
        }

    }

	@Override
	protected String getTargetPath() {
		// TODO Auto-generated method stub
		return "user/" + myAppTokenId + "/" + userEmail + "/get_usertoken_by_usertokenid";
	}

//    @Override
//    protected String run() {
//
//        String responseXML = null;
//        log.trace("CommandGetLastSeenForUserByUserEmail - whydahServiceUri={} myAppTokenId={}, userEmail:{}", tokenServiceUri.toString(), myAppTokenId, userEmail);
//        Client tokenServiceClient = ClientBuilder.newClient();
//        WebTarget userTokenResource = tokenServiceClient.target(tokenServiceUri).path("user").path(myAppTokenId).path(userEmail).path("get_usertoken_by_usertokenid");
//        log.trace("CommandGetLastSeenForUserByUserEmail  - userEmail: {}", userEmail);
//
//        Response response = userTokenResource.request().get();
//        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
//            log.debug("CommandGetLastSeenForUserByUserEmail - Response Code from STS: {}", response.getStatus());
//            throw new IllegalArgumentException("CommandGetLastSeenForUserByUserEmail failed.");
//        }
//        if (!(response.getStatus() == OK.getStatusCode())) {
//            log.debug("CommandGetLastSeenForUserByUserEmail - Response Code from STS: {}", response.getStatus());
//        }
//        responseXML = response.readEntity(String.class);
//        log.debug("CommandGetLastSeenForUserByUserEmail - Response OK with response: {}", responseXML);
//
//        return responseXML;
//
//
//    }

//    @Override
//    protected String getFallback() {
//        log.warn("CommandGetLastSeenForUserByUserEmail - fallback - whydahServiceUri={} - myAppTokenId: {} - userEmail:{}  ", tokenServiceUri.toString(), myAppTokenId, userEmail);
//        return null;
//    }


}
