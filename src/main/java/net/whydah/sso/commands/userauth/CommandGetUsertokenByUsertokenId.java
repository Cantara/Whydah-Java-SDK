package net.whydah.sso.commands.userauth;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import net.whydah.sso.util.ExceptionUtil;
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

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;

public class CommandGetUsertokenByUsertokenId extends HystrixCommand<String> {

    private static final Logger log = LoggerFactory.getLogger(CommandGetUsertokenByUsertokenId.class);

    private URI tokenServiceUri;
    private String myAppTokenId;
    private String usertokenId;
    private String myAppTokenXml;


    public CommandGetUsertokenByUsertokenId(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, String usertokenId) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SSOAUserAuthGroup")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(6000)));
        this.tokenServiceUri = tokenServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.usertokenId = usertokenId;
        this.myAppTokenXml = myAppTokenXml;
        if (tokenServiceUri == null || myAppTokenId == null || myAppTokenXml == null || usertokenId == null) {
            log.error("CommandGetUsertokenByUsertokenId initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, usertokenId:{}", tokenServiceUri.toString(), myAppTokenId, usertokenId);
        }

    }

    @Override
    protected String run() {

        String responseXML = null;
        log.trace("CommandGetUsertokenByUsertokenId - uri={} myAppTokenId={}, usertokenId:{}", tokenServiceUri.toString(), myAppTokenId, usertokenId);


        Client tokenServiceClient = ClientBuilder.newClient();

        WebTarget userTokenResource = tokenServiceClient.target(tokenServiceUri).path("user/" + myAppTokenId + "/get_usertoken_by_usertokenid");
        log.trace("CommandGetUsertokenByUsertokenId  - usertokenid: {} apptoken: {}", usertokenId, myAppTokenXml);
        Form formData = new Form();
        formData.param("apptoken", myAppTokenXml);  //
        formData.param("usertokenid", usertokenId);  //usertokenid

        Response response = userTokenResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            log.debug("CommandGetUsertokenByUsertokenId - Response Code from STS: {}", response.getStatus());
            throw new IllegalArgumentException("CommandGetUsertokenByUsertokenId failed.");
        }
        if (!(response.getStatus() == OK.getStatusCode())) {
            log.debug("CommandGetUsertokenByUsertokenId - Response Code from STS: {}", response.getStatus());
        }
        responseXML = response.readEntity(String.class);
        log.debug("CommandGetUsertokenByUsertokenId - Response OK with XML: {}", responseXML);

        if (responseXML == null) {
            String authenticationFailedMessage = ExceptionUtil.printableUrlErrorMessage("User session failed", userTokenResource, response);
            log.warn(authenticationFailedMessage);
            throw new RuntimeException(authenticationFailedMessage);
        }

        return responseXML;


    }

    @Override
    protected String getFallback() {
        log.warn("CommandGetUsertokenByUsertokenId - fallback - uri={} - usertokenId:{} - myAppTokenId: {}", tokenServiceUri.toString(), usertokenId, myAppTokenId);
        return null;
    }


}