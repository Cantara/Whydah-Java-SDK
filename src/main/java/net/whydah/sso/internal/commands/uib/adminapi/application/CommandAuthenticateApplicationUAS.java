package net.whydah.sso.internal.commands.uib.adminapi.application;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

/**
 * Used by UAS to autenticate application against UIB.
 *
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 2015-11-21.
 */
public class CommandAuthenticateApplicationUAS extends BaseHttpPostHystrixCommand<String> {
    public static final String APP_CREDENTIAL_XML = "appCredentialXml";
    private static final String APPLICATION_AUTH_PATH = "application/auth";

    
    private String appCredentialXml;


    public CommandAuthenticateApplicationUAS(String uibUri, String stsApplicationtokenId, String appCredentialXml) {
        super(URI.create(uibUri), "", stsApplicationtokenId, "UIBApplicationAdminGroup");


        this.appCredentialXml = appCredentialXml;
        if (uibUri == null || stsApplicationtokenId == null || appCredentialXml == null) {
            log.error("{} initialized with null-values - will fail", CommandAuthenticateApplicationUAS.class.getSimpleName());
        }
    }




    @Override
    protected Map<String, String> getFormParameters() {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(APP_CREDENTIAL_XML, appCredentialXml);
    	return super.getFormParameters();
    }
    
	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + APPLICATION_AUTH_PATH;
	}
}
