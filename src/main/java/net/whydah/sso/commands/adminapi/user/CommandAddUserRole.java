package net.whydah.sso.commands.adminapi.user;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.URI;

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
    	return request.contentType("application/json").send(userRoleJson);
    }

//    @Override
//    protected String getFallback() {
//        log.warn("CommandAddUserRole - fallback - whydahServiceUri={}", userAdminServiceUri.toString());
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		// TODO Auto-generated method stub
		return myAppTokenId + "/" + adminUserTokenId + "/user/" +  uId + "/role";
	}


}

