package net.whydah.sso.commands.adminapi.user;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import net.whydah.sso.user.types.UserCredential;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by baardl on 15.06.15.
 */
public class CommandAddUser extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandAddUser.class);
    private URI userAdminServiceUri;
    private String myAppTokenId;
    private String adminUserTokenId;
    private UserCredential userCredential;
    private String userIdentityJson;



    public CommandAddUser(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userIdentityJson) {
        super(HystrixCommandGroupKey.Factory.asKey("UASUserAdminGroup"));
        this.userAdminServiceUri = userAdminServiceUri;
        this.myAppTokenId=myAppTokenId;
        this.adminUserTokenId=adminUserTokenId;
        this.userIdentityJson =userIdentityJson;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userIdentityJson==null ) {
            log.error("CommandAddUser initialized with null-values - will fail");
        }

    }

    @Override
    protected String run() {

        log.trace("CommandAddUser - myAppTokenId={}, adminUserTokenId={{}, userIdentityJson={}", myAppTokenId, adminUserTokenId, userIdentityJson);

        Client tokenServiceClient = ClientBuilder.newClient();

        WebTarget addUser = tokenServiceClient.target(userAdminServiceUri).path(myAppTokenId + "/" + adminUserTokenId + "/user/");
        Response response = addUser.request().post(Entity.json(userIdentityJson));
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            log.warn("CommandAddUser - addUser - User authentication failed with status code " + response.getStatus());
            return null;
            //throw new IllegalArgumentException("Log on failed. " + ClientResponse.Status.FORBIDDEN);
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.readEntity(String.class);
            log.info("CommandAddUser - addUser - Log on OK with response {}", responseXML);
            return responseXML;
        }

        log.warn("CommandAddUser - addUser - failed with status code " + response.getStatus());
        return null;

    }

    @Override
    protected String getFallback() {
        log.warn("CommandAddUser - timeout");
        return null;
    }


}
