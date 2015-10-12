package net.whydah.sso.commands.adminapi.user;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;
import static org.slf4j.LoggerFactory.getLogger;


public class CommandListUsers extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandAddUser.class);
    private URI userAdminServiceUri;
    private String myAppTokenId;
    private String adminUserTokenId;
    private String userQuery;


    public CommandListUsers(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userQuery) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("UASUserAdminGroup")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(3000)));
        this.userAdminServiceUri = userAdminServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.adminUserTokenId = adminUserTokenId;
        this.userQuery = userQuery;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userQuery == null) {
            log.error("CommandListUsers initialized with null-values - will fail");
        }

    }

    @Override
    protected String run() {

        log.trace("CommandListUsers - myAppTokenId={}", myAppTokenId);
        Client uasClient = ClientBuilder.newClient();

        WebTarget userDirectory = uasClient.target(userAdminServiceUri).path(myAppTokenId + "/" + adminUserTokenId + "/users/find/*");

        // Works against UIB, still misisng in UAS...
        Response response = userDirectory.request().get();
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            log.info("CommandListUsers -  userDirectory failed with status code " + response.getStatus());
            return null;
            //throw new IllegalArgumentException("Log on failed. " + ClientResponse.Status.FORBIDDEN);
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseJson = response.readEntity(String.class);
            log.debug("CommandListUsers - Listing users {}", responseJson);
            return responseJson;
        }

        return null;
    }


    @Override
    protected String getFallback() {
        log.warn("CommandListUsers - timeout - uri={}", userAdminServiceUri.toString());
        return null;
    }


}
