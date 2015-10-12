package net.whydah.sso.commands.adminapi.user;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandUpdateUser extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandAddUser.class);
    private URI userAdminServiceUri;
    private String myAppTokenId;
    private String adminUserTokenId;
    private String userJson;


    public CommandUpdateUser(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userJson) {
        super(HystrixCommandGroupKey.Factory.asKey("UASUserAdminGroup"));
        this.userAdminServiceUri = userAdminServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.adminUserTokenId = adminUserTokenId;
        this.userJson = userJson;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userJson == null) {
            log.error("CommandUpdateUser initialized with null-values - will fail");
        }

    }

    @Override
    protected String run() {
        log.trace("CommandUpdateUser - myAppTokenId={}", myAppTokenId);

        Client uasClient = ClientBuilder.newClient();

        WebTarget updateUser = uasClient.target(userAdminServiceUri).path(myAppTokenId + "/" + adminUserTokenId + "/xxx");
        Response response = updateUser.request().post(Entity.json(userJson));
        throw new UnsupportedOperationException();
        //return null;

    }

    @Override
    protected String getFallback() {
        log.warn("CommandUpdateUser - timeout - uri={}", userAdminServiceUri.toString());
        return null;
    }


}
