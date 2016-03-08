package net.whydah.sso.commands.appauth;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.OK;

public class CommandValidateApplicationTokenId extends HystrixCommand<Boolean> {
    private static final Logger log = LoggerFactory.getLogger(CommandValidateApplicationTokenId.class);

    private final String tokenServiceUri;
    private final String applicationTokenId;

    public CommandValidateApplicationTokenId(String tokenServiceUri, String applicationTokenId) {
        super(HystrixCommandGroupKey.Factory.asKey("STSApplicationAdminGroup"));
        this.tokenServiceUri = tokenServiceUri;
        this.applicationTokenId = applicationTokenId;
        if (tokenServiceUri == null || applicationTokenId == null) {
            log.error("CommandValidateUsertokenId initialized with null-values - will fail", CommandValidateApplicationTokenId.class.getSimpleName());
        }
    }

    //TODO ED: Currently only authentication is performed. Should also perform authorization.
    @Override
    protected Boolean run() {
        log.trace("CommandValidateApplicationTokenId - uri={} applicationTokenId={}", tokenServiceUri.toString(), applicationTokenId);

        if (applicationTokenId == null || applicationTokenId.length() < 4) {
            log.warn("CommandValidateApplicationTokenId - Null or too short applicationTokenId={}. return false", applicationTokenId);
            return false;
        }


        Client tokenServiceClient = ClientBuilder.newClient();
        WebTarget verifyResource = tokenServiceClient.target(tokenServiceUri).path(applicationTokenId).path("validate");
        Response response = verifyResource.request().get(Response.class);
        if (response.getStatus() == OK.getStatusCode()) {
            log.info("CommandValidateApplicationTokenId - ApplicationTokenId authentication for {}: {} {}", verifyResource.getUri().toString(), response.getStatusInfo().getStatusCode(), response.getStatusInfo().getReasonPhrase());
            return true;
        }
        if (response.getStatus() == CONFLICT.getStatusCode()) {
            log.warn("CommandValidateApplicationTokenId - ApplicationTokenId authentication for {}: {} {}", verifyResource.getUri().toString(), response.getStatusInfo().getStatusCode(), response.getStatusInfo().getReasonPhrase());
            return false;
        }

        //retry
        log.info("retry...");
        response = verifyResource.request().get(Response.class);
        boolean bolRes = response.getStatus() == OK.getStatusCode();
        log.warn("CommandValidateApplicationTokenId - ApplicationTokenId authentication for {}: {} {}", verifyResource.getUri().toString(), response.getStatusInfo().getStatusCode(), response.getStatusInfo().getReasonPhrase());
        return bolRes;
    }


    @Override
    protected Boolean getFallback() {
        log.warn("CommandValidateApplicationTokenId - fallback - uri={}", tokenServiceUri.toString());
        return false;
    }
}
