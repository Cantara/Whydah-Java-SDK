package net.whydah.sso.commands.threat;


import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.Response.Status.OK;

public class CommandSendThreatSignal extends HystrixCommand<String> {

    private static final Logger log = LoggerFactory.getLogger(CommandSendThreatSignal.class);

    private URI tokenServiceUri;
    private String myAppTokenId;
    private String threatMessage;


    public CommandSendThreatSignal(URI tokenServiceUri, String myAppTokenId, String threatMessage) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("WhydahThreat")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(1000)));
        this.tokenServiceUri = tokenServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.threatMessage = threatMessage;
        if (tokenServiceUri == null || myAppTokenId == null) {
            log.error("CommandSendThreatSignal initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}", tokenServiceUri.toString(), myAppTokenId);
        }
    }

    @Override
    protected String run() {
        log.trace("CommandSendThreatSignal - uri={} myAppTokenId={},", tokenServiceUri.toString(), myAppTokenId);

        Client tokenServiceClient = ClientBuilder.newClient();
        WebTarget userTokenResource = tokenServiceClient.target(tokenServiceUri).path("threat").path(myAppTokenId).path("signal");
        log.trace("CommandSendThreatSignal  -  apptoken: {}", myAppTokenId);
        Form formData = new Form();
        formData.param("signal", threatMessage);
        Response response = userTokenResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        if (!(response.getStatus() == OK.getStatusCode())) {
            log.debug("CommandSendThreatSignal - Response Code from STS: {}", response.getStatus());
        }
        return "";


    }

    @Override
    protected String getFallback() {
        log.warn("CommandSendThreatSignal - fallback - uri={} -  myAppTokenId: {}", tokenServiceUri.toString(), myAppTokenId);
        return null;
    }


}

