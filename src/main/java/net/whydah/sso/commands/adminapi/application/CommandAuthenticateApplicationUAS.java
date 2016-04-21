package net.whydah.sso.commands.adminapi.application;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Used by UAS to autenticate application against UIB.
 *
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 2015-11-21.
 */
public class CommandAuthenticateApplicationUAS extends BaseHttpPostHystrixCommand<Response> {
    public static final String APP_CREDENTIAL_XML = "appCredentialXml";
    private static final String APPLICATION_AUTH_PATH = "application/auth";
 
    private String uibUri;
    private String stsApplicationtokenId;
    private String appCredentialXml;


    public CommandAuthenticateApplicationUAS(String uibUri, String stsApplicationtokenId, String appCredentialXml) {
        super(URI.create(uibUri), "", stsApplicationtokenId, "UIBApplicationAdminGroup");
        this.uibUri = uibUri;
        this.stsApplicationtokenId = stsApplicationtokenId;
        this.appCredentialXml = appCredentialXml;
        if (uibUri == null || stsApplicationtokenId == null || appCredentialXml == null) {
            log.error("{} initialized with null-values - will fail", CommandAuthenticateApplicationUAS.class.getSimpleName());
        }
    }

//    @Override
//    protected Response run() {
//        log.trace("{} - stsApplicationtokenId={}, ", CommandAuthenticateApplicationUAS.class.getSimpleName(), stsApplicationtokenId);
//        Client client = ClientBuilder.newClient();
//        WebTarget uib = client.target(uibUri);
//        WebTarget webResource = uib.path(stsApplicationtokenId).path(APPLICATION_AUTH_PATH);
//        MultivaluedMap<String,String> formData = new MultivaluedHashMap<>(2);
//        formData.add(APP_CREDENTIAL_XML, appCredentialXml);
//        return webResource.request(MediaType.APPLICATION_FORM_URLENCODED)
//                          .post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED));
//    }



    @Override
    protected Map<String, String> getFormParameters() {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(APP_CREDENTIAL_XML, appCredentialXml);
    	return super.getFormParameters();
    }
    
	@Override
	protected String getTargetPath() {
		return null;
	}
}
