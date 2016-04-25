package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.types.ApplicationCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class CommandLogonApplicationJersey /**extends HystrixCommand<String> */
{

    private static final Logger log = LoggerFactory.getLogger(CommandLogonApplication.class);
    private URI tokenServiceUri;
    private ApplicationCredential appCredential;

    /**
    public CommandLogonApplicationJersey(URI tokenServiceUri, ApplicationCredential appCredential) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("STSApplicationAdminGroup")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(3000)));

        this.tokenServiceUri = tokenServiceUri;
        this.appCredential = appCredential;
        if (tokenServiceUri == null || appCredential == null) {
            log.error("CommandLogonApplication initialized with null-values - will fail");
            throw new IllegalArgumentException("Missing parameters for \n" +
                    "\ttokenServiceUri [" + tokenServiceUri + "], \n" +
                    "\tappCredential [" + appCredential + "]");
        }
        HystrixRequestContext.initializeContext();

    }

    @Override
    protected String run() {
        log.trace("CommandLogonApplication - whydahServiceUri={} appCredential={}", tokenServiceUri.toString(), ApplicationCredentialMapper.toXML(appCredential));

        Client tokenServiceClient;
        if (!SSLTool.isCertificateCheckDisabled()) {
            tokenServiceClient = ClientBuilder.newClient();
        } else {
            tokenServiceClient = ClientBuilder.newBuilder().sslContext(SSLTool.sc).hostnameVerifier((s1, s2) -> true).build();

        }


        Form formData = new Form();
        formData.param("applicationcredential", ApplicationCredentialMapper.toXML(appCredential));

        Response response;
        WebTarget logonResource = tokenServiceClient.target(tokenServiceUri).path("logon");
        try {
    response = logonResource.request().post(Entity.entity(formData, HttpSender.APPLICATION_FORM_URLENCODED), Response.class);
//            response = postForm(formData, logonResource);
        } catch (RuntimeException e) {
            log.error("CommandLogonApplication - logonApplication - Problem connecting to {}", logonResource.toString());
            log.error(e.toString());
            throw (e);
        }
        if (response.getStatus() != 200) {
            log.error("CommandLogonApplication - Application authentication failed with statuscode {}", response.getStatus());
            throw new RuntimeException("CommandLogonApplication - Application authentication failed");
        } else {
            String myAppTokenXml = response.readEntity(String.class);
            log.debug("CommandLogonApplication - Applogon ok: apptokenxml: {}", myAppTokenXml);
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            log.trace("CommandLogonApplication - myAppTokenId: {}", myApplicationTokenID);
            return myAppTokenXml;
        }
    }

    private Response postForm(Form formData, WebTarget logonResource) {
    return logonResource.request().post(Entity.entity(formData, HttpSender.APPLICATION_FORM_URLENCODED), Response.class);
    }

    @Override
    protected String getFallback() {
        log.warn("CommandLogonApplication - fallback - whydahServiceUri={}", tokenServiceUri.toString());
        return null;
    }
     */

}