package net.whydah.sso.commands.adminapi.user;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
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
public class CommandAddUserRole extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandAddUser.class);
    private URI userAdminServiceUri;
    private String myAppTokenId;
    private String adminUserTokenId;
    private String userRoleJson;
    private String uId;


    public CommandAddUserRole(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String uId,String roleJson) {
        super(HystrixCommandGroupKey.Factory.asKey("UASUserAdminGroup"));
        this.userAdminServiceUri = userAdminServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.adminUserTokenId = adminUserTokenId;
        this.userRoleJson = roleJson;
        this.uId=uId;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || roleJson == null) {
            log.error("CommandAddUserRole initialized with null-values - will fail");
        }

    }

    @Override
    protected String run() {

        log.trace("CommandAddUserRole - myAppTokenId={} - userRoleJson={}", myAppTokenId, userRoleJson);

        Client uasClient = ClientBuilder.newClient();

        WebTarget addUserRole = uasClient.target(userAdminServiceUri).path(myAppTokenId).path(adminUserTokenId).path("user").path(uId).path("role");
        Response response = addUserRole.request(MediaType.APPLICATION_JSON).post(Entity.entity(userRoleJson, MediaType.APPLICATION_JSON));
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            log.info("CommandAddUserRole - addUserRole - User authentication failed with status code " + response.getStatus());
            return null;
            //throw new IllegalArgumentException("Log on failed. " + ClientResponse.Status.FORBIDDEN);
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseJson = response.readEntity(String.class);
            log.debug("CommandAddUserRole - addUserRole - Log on OK with response {}", responseJson);
            return responseJson;
        }

        return null;

    }

    @Override
    protected String getFallback() {
        log.warn("CommandAddUserRole - timeout - uri={}", userAdminServiceUri.toString());
        return null;
    }


}

