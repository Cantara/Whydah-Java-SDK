package net.whydah.sso.commands.adminapi.user;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;


public class CommandGetLastSeenForUserByUserEmail extends HystrixCommand<String> {

    private static final Logger log = LoggerFactory.getLogger(CommandGetLastSeenForUserByUserEmail.class);

    private URI tokenServiceUri;
    private String myAppTokenId;
    private String userEmail;


    public CommandGetLastSeenForUserByUserEmail(URI tokenServiceUri, String myAppTokenId, String userEmail) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("STSUserQueries")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(6000)));
        this.tokenServiceUri = tokenServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.userEmail = userEmail;
        if (tokenServiceUri == null || myAppTokenId == null || userEmail == null) {
            log.error("CommandGetLastSeenForUserByUserEmail initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, userEmail:{}", tokenServiceUri.toString(), myAppTokenId, userEmail);
        }

    }

    @Override
    protected String run() {

        String responseXML = null;
        log.trace("CommandGetLastSeenForUserByUserEmail - uri={} myAppTokenId={}, userEmail:{}", tokenServiceUri.toString(), myAppTokenId, userEmail);
        Client tokenServiceClient = ClientBuilder.newClient();
        WebTarget userTokenResource = tokenServiceClient.target(tokenServiceUri).path("user").path(myAppTokenId).path(userEmail).path("get_usertoken_by_usertokenid");
        log.trace("CommandGetLastSeenForUserByUserEmail  - userEmail: {}", userEmail);

        Response response = userTokenResource.request().get();
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            log.debug("CommandGetLastSeenForUserByUserEmail - Response Code from STS: {}", response.getStatus());
            throw new IllegalArgumentException("CommandGetLastSeenForUserByUserEmail failed.");
        }
        if (!(response.getStatus() == OK.getStatusCode())) {
            log.debug("CommandGetLastSeenForUserByUserEmail - Response Code from STS: {}", response.getStatus());
        }
        responseXML = response.readEntity(String.class);
        log.debug("CommandGetLastSeenForUserByUserEmail - Response OK with response: {}", responseXML);

        return responseXML;


    }

    @Override
    protected String getFallback() {
        log.warn("CommandGetLastSeenForUserByUserEmail - timeout - uri={} - myAppTokenId: {} - userEmail:{}  ", tokenServiceUri.toString(), myAppTokenId, userEmail);
        return null;
    }


}
