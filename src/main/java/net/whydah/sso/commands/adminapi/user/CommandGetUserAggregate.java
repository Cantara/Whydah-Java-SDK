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

public class CommandGetUserAggregate extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandAddUser.class);
    private URI userAdminServiceUri;
    private String myAppTokenId;
    private String adminUserTokenId;
    private String userID;


    public CommandGetUserAggregate(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userID) {
        super(HystrixCommandGroupKey.Factory.asKey("UASUserAdminGroup"));
        this.userAdminServiceUri = userAdminServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.adminUserTokenId = adminUserTokenId;
        this.userID = userID;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userID == null) {
            log.error("CommandGetUserAggregate initialized with null-values - will fail");
        }

    }

    @Override
    protected String run() {
        log.trace("CommandGetUserAggregate - myAppTokenId={}", myAppTokenId);

        Client uasClient = ClientBuilder.newClient();

        WebTarget updateUser = uasClient.target(userAdminServiceUri).path(myAppTokenId).path(adminUserTokenId).path("useraggregate").path(userID);
        Response response = updateUser.request().get();
        if (response.getStatus() == OK.getStatusCode()) {
            String responseJson = response.readEntity(String.class);
            log.debug("CommandGetUserAggregate - Returning user {}", responseJson);
            return responseJson;
        }
        return null;


    }

    @Override
    protected String getFallback() {
        log.warn("CommandGetUserAggregate - timeout - uri={}", userAdminServiceUri.toString());
        return null;
    }


}
