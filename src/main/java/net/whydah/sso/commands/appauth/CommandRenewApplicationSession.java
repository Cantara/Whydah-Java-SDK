package net.whydah.sso.commands.appauth;


import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
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

public class CommandRenewApplicationSession extends HystrixCommand<String> {

    private static final Logger log = LoggerFactory.getLogger(CommandRenewApplicationSession.class);
    private URI tokenServiceUri;
    private String applicationtokenid;

    public CommandRenewApplicationSession(URI tokenServiceUri, String applicationtokenid) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("STSApplicationAdminGroup")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(3000)));

        this.tokenServiceUri = tokenServiceUri;
        this.applicationtokenid = applicationtokenid;
        if (tokenServiceUri == null || applicationtokenid == null) {
            log.error("CommandRenewApplicationSession initialized with null-values - will fail");
            throw new IllegalArgumentException("Missing parameters for \n" +
                    "\ttokenServiceUri [" + tokenServiceUri + "], \n" +
                    "\tapplicationtokenid [" + applicationtokenid + "]");
        }
        HystrixRequestContext.initializeContext();

    }


    @Override
    protected String run() {
        log.trace("CommandRenewApplicationSession - uri={} applicationtokenid={}", tokenServiceUri.toString(), applicationtokenid);

        Client tokenServiceClient = ClientBuilder.newClient();
        Form formData = new Form();

        Response response;
        WebTarget applicationRenewResource = tokenServiceClient.target(tokenServiceUri).path(applicationtokenid).path("renew_applicationtoken");
        try {
            response = postForm(formData, applicationRenewResource);
        } catch (RuntimeException e) {
            log.error("CommandRenewApplicationSession - renew_applicationtoken - Problem connecting to {}", applicationRenewResource.toString());
            throw (e);
        }
        String myAppTokenXml = response.readEntity(String.class);
        log.trace("CommandRenewApplicationSession - Applogon ok: apptokenxml: {}", myAppTokenXml);
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        log.debug("CommandRenewApplicationSession - myAppTokenId: {}", myApplicationTokenID);
        return myAppTokenXml;
    }

    private Response postForm(Form formData, WebTarget applicationRenewResource) {
        return applicationRenewResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
    }

    @Override
    protected String getFallback() {
        log.warn("CommandRenewApplicationSession - fallback - uri={}", tokenServiceUri.toString());
        return null;
    }

}