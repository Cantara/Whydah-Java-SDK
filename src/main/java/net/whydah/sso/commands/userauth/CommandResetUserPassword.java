package net.whydah.sso.commands.userauth;

import java.net.URI;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 2015-11-21.
 */
public class CommandResetUserPassword extends BaseHttpPostHystrixCommand<Response> {
   
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
