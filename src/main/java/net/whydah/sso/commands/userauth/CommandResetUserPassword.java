package net.whydah.sso.commands.userauth;

import java.net.URI;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

/**
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 2015-11-21.
 */
public class CommandResetUserPassword extends BaseHttpPostHystrixCommand<String> {
   
    private String uid;

    public CommandResetUserPassword(String uibUri, String applicationtokenId, String uid) {
    	super(URI.create(uibUri), "", applicationtokenId,"SSOAUserAuthGroup");
       
        this.uid = uid;
        if (uibUri == null || applicationtokenId == null || uid == null) {
            log.error("{} initialized with null-values - will fail", CommandResetUserPassword.class.getSimpleName());
        }
    }
    
    

//    @Override
//    protected Response run() {
//        log.trace("{} - applicationtokenId={}, ", CommandResetUserPassword.class.getSimpleName(), applicationtokenId);
//        Client client = ClientBuilder.newClient();
//        WebTarget uib = client.target(uibUri);
//        WebTarget webResource = uib.path(applicationtokenId).path("user").path(uid).path("reset_password");
//        return webResource.request().post(null);
//    }
//
//    @Override
//    protected Response getFallback() {
//        log.warn("{} - fallback - uibUri={}", CommandResetUserPassword.class.getSimpleName(), uibUri);
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/user/" + uid + "/reset_password";
	}
}
