package net.whydah.sso.commands.appauth;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.OK;

public class CommandGetApplicationIdFromApplicationTokenId extends HystrixCommand<String> {
    private static final Logger log = LoggerFactory.getLogger(CommandGetApplicationIdFromApplicationTokenId.class);

    private final URI tokenServiceUri;
    private final String applicationTokenId;

    public CommandGetApplicationIdFromApplicationTokenId(URI tokenServiceUri, String applicationTokenId) {
        super(HystrixCommandGroupKey.Factory.asKey("STSApplicationAdminGroup"));
        this.tokenServiceUri = tokenServiceUri;
        this.applicationTokenId = applicationTokenId;
        if (tokenServiceUri == null || applicationTokenId == null) {
            log.error("ComandGetApplicationIDFromApplicationTokenId initialized with null-values - will fail", CommandGetApplicationIdFromApplicationTokenId.class.getSimpleName());
        }
    }

    @Override
    protected String run() {
        log.trace("ComandGetApplicationIDFromApplicationTokenId - uri={} applicationTokenId={}", tokenServiceUri.toString(), applicationTokenId);

        if (applicationTokenId == null || applicationTokenId.length() < 4) {
            log.warn("ComandGetApplicationIDFromApplicationTokenId - Null or too short applicationTokenId={}. return false", applicationTokenId);
            return null;
        }


        Client tokenServiceClient = ClientBuilder.newClient();
        WebTarget verifyResource = tokenServiceClient.target(tokenServiceUri).path(applicationTokenId).path("get_application_id");
        Response response = verifyResource.request().get(Response.class);
        if (response.getStatus() == OK.getStatusCode()) {
            log.info("ComandGetApplicationIDFromApplicationTokenId - ApplicationTokenId authentication for {}: {} {}", verifyResource.getUri().toString(), response.getStatusInfo().getStatusCode(), response.getStatusInfo().getReasonPhrase());
            return response.readEntity(String.class);
        }
        if (response.getStatus() == CONFLICT.getStatusCode()) {
            log.warn("ComandGetApplicationIDFromApplicationTokenId - ApplicationTokenId authentication for {}: {} {}", verifyResource.getUri().toString(), response.getStatusInfo().getStatusCode(), response.getStatusInfo().getReasonPhrase());
        }
        return null;

    }


    @Override
    protected String getFallback() {
        log.warn("ComandGetApplicationIDFromApplicationTokenId - timeout - uri={}", tokenServiceUri.toString());
        return null;
    }
}
