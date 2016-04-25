package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;

public class CommandValidateUsertokenId extends BaseHttpGetHystrixCommand<Boolean> {

    private String usertokenid;

    public CommandValidateUsertokenId(URI tokenServiceUri, String myAppTokenId, String usertokenid) {
    	super(tokenServiceUri, "", myAppTokenId, "SSOUserAuthGroup");
        this.usertokenid=usertokenid;
        if (tokenServiceUri == null || myAppTokenId == null || usertokenid == null  ) {
            log.error("CommandValidateUsertokenId initialized with null-values - will fail");
        }
    }

//    @Override
//    protected Boolean run() {
//        log.trace("CommandValidateUsertokenId - whydahServiceUri={} myAppTokenId={}", tokenServiceUri.toString(), myAppTokenId);
//
//        if (usertokenid == null || usertokenid.length() < 4) {
//            log.warn("CommandValidateUsertokenId - Called with bogus usertokenid={}. return false", usertokenid);
//            return false;
//        }
//        // logonApplication();
//        Client tokenServiceClient = ClientBuilder.newClient();
//        WebTarget verifyResource = tokenServiceClient.target(tokenServiceUri).path("user/" + myAppTokenId + "/validate_usertokenid/" + usertokenid);
//        Response response = get(verifyResource);
//        if (response.getStatus() == OK.getStatusCode()) {
//            log.info("CommandValidateUsertokenId - validate_usertokenid {}  result {}", verifyResource.getUri().toString(), response);
//            return true;
//        }
//        if (response.getStatus() == CONFLICT.getStatusCode()) {
//            log.warn("CommandValidateUsertokenId - usertokenid not ok: {}", response);
//            return false;
//        }
//        //retry
//        log.info("CommandValidateUsertokenId - retrying usertokenid ");
//        //logonApplication();
//        response = get(verifyResource);
//        boolean bolRes = response.getStatus() == OK.getStatusCode();
//        log.info("CommandValidateUsertokenId - validate_usertokenid {}  result {}", verifyResource.getUri().toString(), response);
//        return bolRes;
//    }
//
//    private Response get(WebTarget verifyResource) {
//        return verifyResource.request().get(Response.class);
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
	protected String getTargetPath() {
		return "user/" + myAppTokenId + "/validate_usertokenid/" + usertokenid;
	}


}
