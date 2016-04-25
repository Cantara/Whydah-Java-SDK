package net.whydah.sso.commands.appauth;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;

public class CommandValidateApplicationTokenId extends BaseHttpGetHystrixCommand<Boolean> {


	int retryCnt = 0;

//    @Override
//    protected Boolean run() {
//        log.trace("CommandValidateApplicationTokenId - whydahServiceUri={} applicationTokenId={}", tokenServiceUri.toString(), applicationTokenId);
//
//        if (applicationTokenId == null || applicationTokenId.length() < 4) {
//            log.warn("CommandValidateApplicationTokenId - Null or too short applicationTokenId={}. return false", applicationTokenId);
//            return false;
//        }
//
//
//        Client tokenServiceClient = ClientBuilder.newClient();
//        WebTarget verifyResource = tokenServiceClient.target(tokenServiceUri).path(applicationTokenId).path("validate");
//        Response response = verifyResource.request().get(Response.class);
//        if (response.getStatus() == OK.getStatusCode()) {
//            log.info("CommandValidateApplicationTokenId - ApplicationTokenId authentication for {}: {} {}", verifyResource.getUri().toString(), response.getStatusInfo().getStatusCode(), response.getStatusInfo().getReasonPhrase());
//            return true;
//        }
//        if (response.getStatus() == CONFLICT.getStatusCode()) {
//            log.warn("CommandValidateApplicationTokenId - ApplicationTokenId authentication for {}: {} {}", verifyResource.getUri().toString(), response.getStatusInfo().getStatusCode(), response.getStatusInfo().getReasonPhrase());
//            return false;
//        }
//
//        //retry
//        log.info("retry...");
//        response = verifyResource.request().get(Response.class);
//        boolean bolRes = response.getStatus() == OK.getStatusCode();
//        log.warn("CommandValidateApplicationTokenId - ApplicationTokenId authentication for {}: {} {}", verifyResource.getUri().toString(), response.getStatusInfo().getStatusCode(), response.getStatusInfo().getReasonPhrase());
//        return bolRes;
//    }

	public CommandValidateApplicationTokenId(String tokenServiceUri, String applicationTokenId) {
		super(URI.create(tokenServiceUri), "", applicationTokenId, "STSApplicationAdminGroup");

		if (tokenServiceUri == null || applicationTokenId == null) {
			log.error("CommandValidateUsertokenId initialized with null-values - will fail", CommandValidateApplicationTokenId.class.getSimpleName());
		}
	}


//    @Override
//    protected Boolean getFallback() {
//        log.warn("CommandValidateApplicationTokenId - fallback - whydahServiceUri={}", tokenServiceUri.toString());
//        return false;
//    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/validate";
	}
}
