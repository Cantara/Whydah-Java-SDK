package net.whydah.sso.commands.appauth;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.mappers.ApplicationCredentialMapper;
import net.whydah.sso.application.types.ApplicationCredential;
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

public class CommandLogonApplication extends HystrixCommand<String> {

    private static final Logger log = LoggerFactory.getLogger(CommandLogonApplication.class);
    private URI tokenServiceUri ;
    private ApplicationCredential appCredential ;

    public CommandLogonApplication(URI tokenServiceUri,ApplicationCredential appCredential) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("STSApplicationAdminGroup")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(6000)));

        this.tokenServiceUri = tokenServiceUri;
        this.appCredential=appCredential;
        if (tokenServiceUri==null || appCredential==null ){
            log.error("CommandLogonApplication initialized with null-values - will fail");
            throw new IllegalArgumentException("Missing parameters for \n" +
                    "\ttokenServiceUri ["+ tokenServiceUri + "], \n" +
                    "\tappCredential ["+ appCredential + "]");
        }
        HystrixRequestContext.initializeContext();

    }

    @Override
    protected String run() {
        log.trace("CommandLogonApplication - uri={} appCredential={}", tokenServiceUri.toString(), ApplicationCredentialMapper.toXML(appCredential));

        Client tokenServiceClient = ClientBuilder.newClient();
        Form formData = new Form();
        formData.param("applicationcredential", ApplicationCredentialMapper.toXML(appCredential));

        Response response;
        WebTarget logonResource = tokenServiceClient.target(tokenServiceUri).path("logon");
        try {
//            response = logonResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
            response = postForm(formData, logonResource);
        } catch (RuntimeException e) {
            log.error("CommandLogonApplication - logonApplication - Problem connecting to {}", logonResource.toString());
            throw(e);
        }
        if (response.getStatus() != OK.getStatusCode()) {
            log.warn("CommandLogonApplication - Application authentication failed with statuscode {} - retrying ", response.getStatus());
            try {
//                WebResource logonResource = tokenServiceClient.resource(tokenServiceUri).path("logon");
                response = postForm(formData,logonResource);
            } catch (RuntimeException e) {
                log.error("CommandLogonApplication - logonApplication - Problem connecting to {}", logonResource.toString());
                throw(e);
            }
            if (response.getStatus() != 200) {
                log.error("CommandLogonApplication - Application authentication failed with statuscode {}", response.getStatus());
                throw new RuntimeException("CommandLogonApplication - Application authentication failed");
            }
        }
        String myAppTokenXml = response.readEntity(String.class);
        log.debug("CommandLogonApplication - Applogon ok: apptokenxml: {}", myAppTokenXml);
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        log.trace("CommandLogonApplication - myAppTokenId: {}", myApplicationTokenID);
        return myAppTokenXml;
    }

    private Response postForm(Form formData, WebTarget logonResource) {
        return logonResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
    }

    @Override
    protected String getFallback() {
        log.warn("CommandLogonApplication - timeout - uri={}", tokenServiceUri.toString());
        return null;
    }

}