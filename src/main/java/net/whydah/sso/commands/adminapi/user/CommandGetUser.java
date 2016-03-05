package net.whydah.sso.commands.adminapi.user;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.Response.Status.OK;
import static org.slf4j.LoggerFactory.getLogger;

public class CommandGetUser extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandAddUser.class);
    private URI userAdminServiceUri;
    private String myAppTokenId;
    private String adminUserTokenId;
    private String userID;


    public CommandGetUser(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userID) {
        super(HystrixCommandGroupKey.Factory.asKey("UASUserAdminGroup"));
        this.userAdminServiceUri = userAdminServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.adminUserTokenId = adminUserTokenId;
        this.userID = userID;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userID == null) {
            log.error("CommandGetUser initialized with null-values - will fail");
        }

    }

    @Override
    protected String run() {
        log.trace("CommandGetUser - myAppTokenId={}", myAppTokenId);

        Client uasClient = ClientBuilder.newClient();

        WebTarget getUser = uasClient.target(userAdminServiceUri).path(myAppTokenId).path(adminUserTokenId).path("user").path(userID);
        Response response = getUser.request().get();
        if (response.getStatus() == OK.getStatusCode()) {
            String responseJson = response.readEntity(String.class);
            log.debug("CommandGetUser - Returning user {}", responseJson);
            return responseJson;
        }
        return null;


    }

    @Override
    protected String getFallback() {
        log.warn("CommandGetUser - timeout - uri={}", userAdminServiceUri.toString());
        return null;
    }


}
