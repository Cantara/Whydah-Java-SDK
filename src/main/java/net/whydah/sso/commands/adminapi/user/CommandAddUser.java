package net.whydah.sso.commands.adminapi.user;

import com.github.kevinsawicki.http.HttpRequest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.BaseHttpPostHystrixCommand;

import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.net.URI;

import static javax.ws.rs.core.Response.Status.OK;
import static org.slf4j.LoggerFactory.getLogger;

public class CommandAddUser extends BaseHttpPostHystrixCommand<String> {
    private static final Logger log = getLogger(CommandAddUser.class);
    private URI userAdminServiceUri;
    private String myAppTokenId;
    private String adminUserTokenId;
    private UserCredential userCredential;
    private String userIdentityJson;

    public CommandAddUser(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userIdentityJson) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup");

        this.adminUserTokenId=adminUserTokenId;
        this.userIdentityJson =userIdentityJson;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userIdentityJson==null ) {
            log.error(TAG + " initialized with null-values - will fail - userAdminServiceUri:{}, myAppTokenId:{}, adminUserTokenId:{}, userIdentityJson:{}", userAdminServiceUri, myAppTokenId, adminUserTokenId, userIdentityJson);
        }
    }
//
//    @Override
//    protected String run() {
//
//        log.trace(TAG + " - myAppTokenId={}, adminUserTokenId={{}, userIdentityJson={}", myAppTokenId, adminUserTokenId, userIdentityJson);
//        Client tokenServiceClient = ClientBuilder.newClient();
//        WebTarget addUser = tokenServiceClient.target(userAdminServiceUri).path(myAppTokenId).path(adminUserTokenId).path("user");
//        Response response = addUser.request().post(Entity.json(userIdentityJson));
//        if (response.getStatus() == OK.getStatusCode()) {
//            String responseXML = response.readEntity(String.class);
//            log.info("CommandAddUser - addUser - Create user OK with response {}", responseXML);
//            return responseXML;
//        }
//
//        log.warn("CommandAddUser - addUser - failed with status code " + response.getStatus());
//        return null;
//
//    }
    
    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
    
    	request.send(userIdentityJson);
    	return request;
    }

//    @Override
//    protected String getFallback() {
//        log.warn("CommandAddUser - fallback - uri={}", userAdminServiceUri.toString());
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + adminUserTokenId  +  "/user";
	}


}
