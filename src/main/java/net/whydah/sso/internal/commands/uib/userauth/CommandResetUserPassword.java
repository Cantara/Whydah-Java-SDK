package net.whydah.sso.internal.commands.uib.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.URI;

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
    
    

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/user/" + uid + "/reset_password";
	}
}
