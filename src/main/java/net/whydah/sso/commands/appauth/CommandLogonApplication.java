package net.whydah.sso.commands.appauth;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.mappers.ApplicationCredentialMapper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.util.BaseHttpPostHystrixCommand;
import net.whydah.sso.util.SSLTool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLogonApplication extends BaseHttpPostHystrixCommand<String> {

	private static final Logger log = LoggerFactory.getLogger(CommandLogonApplication.class);
	private URI tokenServiceUri;
	private ApplicationCredential appCredential;

	public CommandLogonApplication(URI tokenServiceUri, ApplicationCredential appCredential) {
		super(tokenServiceUri, "", "", "STSApplicationAdminGroup", 3000);
		this.appCredential = appCredential;
	}

	@Override
	protected String getFallback() {
		log.warn("CommandLogonApplication - fallback - uri={}", tokenServiceUri.toString());
		return null;
	}



	@Override
	protected String getTargetPath() {
		return "logon";
	}
//
//	@Override
//    protected String run() {
//        log.trace("CommandLogonApplication - uri={} appCredential={}", tokenServiceUri.toString(), ApplicationCredentialMapper.toXML(appCredential));
//
//        Client tokenServiceClient;
//        if (!SSLTool.isCertificateCheckDisabled()) {
//            tokenServiceClient = ClientBuilder.newClient();
//        } else {
//            tokenServiceClient = ClientBuilder.newBuilder().sslContext(SSLTool.sc).hostnameVerifier((s1, s2) -> true).build();
//
//        }
//
//
//        Form formData = new Form();
//        formData.param("applicationcredential", ApplicationCredentialMapper.toXML(appCredential));
//
//        Response response;
//        WebTarget logonResource = tokenServiceClient.target(tokenServiceUri).path("logon");
//        try {
//            response = logonResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
////            response = postForm(formData, logonResource);
//        } catch (RuntimeException e) {
//            log.error("CommandLogonApplication - logonApplication - Problem connecting to {}", logonResource.toString());
//            log.error(e.toString());
//            throw (e);
//        }
//        if (response.getStatus() != 200) {
//            log.error("CommandLogonApplication - Application authentication failed with statuscode {}", response.getStatus());
//            throw new RuntimeException("CommandLogonApplication - Application authentication failed");
//        } else {
//            String myAppTokenXml = response.readEntity(String.class);
//            log.debug("CommandLogonApplication - Applogon ok: apptokenxml: {}", myAppTokenXml);
//            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
//            log.trace("CommandLogonApplication - myAppTokenId: {}", myApplicationTokenID);
//            return myAppTokenXml;
//        }
//    }
//
//    private Response postForm(Form formData, WebTarget logonResource) {
//        return logonResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
//    }


	@Override
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("applicationcredential", ApplicationCredentialMapper.toXML(appCredential));
		return data;
	}

}
