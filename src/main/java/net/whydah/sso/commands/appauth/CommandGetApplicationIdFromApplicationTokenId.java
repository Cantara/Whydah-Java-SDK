package net.whydah.sso.commands.appauth;

import java.net.URI;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandGetApplicationIdFromApplicationTokenId extends BaseHttpGetHystrixCommand<String> {
    private static final Logger log = LoggerFactory.getLogger(CommandGetApplicationIdFromApplicationTokenId.class);



    public CommandGetApplicationIdFromApplicationTokenId(URI tokenServiceUri, String applicationTokenId) {
    	super(tokenServiceUri, "", applicationTokenId, "STSApplicationAdminGroup");

        if (tokenServiceUri == null || applicationTokenId == null) {
            log.error("ComandGetApplicationIDFromApplicationTokenId initialized with null-values - will fail", CommandGetApplicationIdFromApplicationTokenId.class.getSimpleName());
        }
    }

//    @Override
//    protected String run() {
//        log.trace("ComandGetApplicationIDFromApplicationTokenId - uri={} applicationTokenId={}", tokenServiceUri.toString(), applicationTokenId);
//
//        if (applicationTokenId == null || applicationTokenId.length() < 4) {
//            log.warn("ComandGetApplicationIDFromApplicationTokenId - Null or too short applicationTokenId={}. return false", applicationTokenId);
//            return null;
//        }
//
//
//        Client tokenServiceClient = ClientBuilder.newClient();
//        WebTarget verifyResource = tokenServiceClient.target(tokenServiceUri).path(applicationTokenId).path("get_application_id");
//        Response response = verifyResource.request().get(Response.class);
//        if (response.getStatus() == OK.getStatusCode()) {
//            log.info("ComandGetApplicationIDFromApplicationTokenId - ApplicationTokenId authentication for {}: {} {}", verifyResource.getUri().toString(), response.getStatusInfo().getStatusCode(), response.getStatusInfo().getReasonPhrase());
//            return response.readEntity(String.class);
//        }
//        if (response.getStatus() == CONFLICT.getStatusCode()) {
//            log.warn("ComandGetApplicationIDFromApplicationTokenId - ApplicationTokenId authentication for {}: {} {}", verifyResource.getUri().toString(), response.getStatusInfo().getStatusCode(), response.getStatusInfo().getReasonPhrase());
//        }
//        return null;
//
//    }
//
//
//    @Override
//    protected String getFallback() {
//        log.warn("ComandGetApplicationIDFromApplicationTokenId - fallback - uri={}", tokenServiceUri.toString());
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/get_application_id";
	}
}
