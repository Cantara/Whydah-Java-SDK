package net.whydah.sso.commands.adminapi.user;

import com.github.kevinsawicki.http.HttpRequest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import net.whydah.sso.util.BaseHttpPostHystrixCommand;

import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.URI;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by totto on 24.06.15.
 */
public class CommandAddUserRole extends BaseHttpPostHystrixCommand<String> {
    
  

    private String adminUserTokenId;
    private String userRoleJson;
    private String uId;


    public CommandAddUserRole(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String uId,String roleJson) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup");
        
        this.adminUserTokenId = adminUserTokenId;
        this.userRoleJson = roleJson;
        this.uId=uId;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || roleJson == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }

    }

//    @Override
//    protected String run() {
//
//        log.trace("CommandAddUserRole - myAppTokenId={} - userRoleJson={}", myAppTokenId, userRoleJson);
//
//        Client uasClient = ClientBuilder.newClient();
//
//        WebTarget addUserRole = uasClient.target(userAdminServiceUri).path(myAppTokenId).path(adminUserTokenId).path("user").path(uId).path("role");
//        Response response = addUserRole.request(MediaType.APPLICATION_JSON).post(Entity.entity(userRoleJson, MediaType.APPLICATION_JSON));
//        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
//            log.info("CommandAddUserRole - addUserRole - User authentication failed with status code " + response.getStatus());
//            return null;
//            //throw new IllegalArgumentException("Log on failed. " + ClientResponse.Status.FORBIDDEN);
//        }
//        if (response.getStatus() == OK.getStatusCode()) {
//            String responseJson = response.readEntity(String.class);
//            log.debug("CommandAddUserRole - addUserRole - Log on OK with response {}", responseJson);
//            return responseJson;
//        }
//
//        return null;
//
//    }
    
    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
    	request = request.send(userRoleJson);
    	return request;
    }

//    @Override
//    protected String getFallback() {
//        log.warn("CommandAddUserRole - fallback - uri={}", userAdminServiceUri.toString());
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		// TODO Auto-generated method stub
		return myAppTokenId + "/" + adminUserTokenId + "/user/" +  uId + "/role";
	}


}

