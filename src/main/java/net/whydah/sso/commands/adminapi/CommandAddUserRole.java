package net.whydah.sso.commands.adminapi;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by totto on 24.06.15.
 */
public class CommandAddUserRole extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandAddUser.class);
    private URI userAdminServiceUri;
    private String myAppTokenId;
    private String adminUserTokenId;
    private String roleJson;


    public CommandAddUserRole(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String roleJson) {
        super(HystrixCommandGroupKey.Factory.asKey("UASUserGroup"));
        this.userAdminServiceUri = userAdminServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.adminUserTokenId = adminUserTokenId;
        this.roleJson = roleJson;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || roleJson == null) {
            log.error("CommandAddUserRole initialized with null-values - will fail");
        }

    }

    @Override
    protected String run() {

        log.trace("CommandAddUserRole - myAppTokenId={}", myAppTokenId);

        Client tokenServiceClient = ClientBuilder.newClient();

        WebTarget addUser = tokenServiceClient.target(userAdminServiceUri).path(myAppTokenId + "/" + adminUserTokenId + "/xxx");
        // Response response = addUser.request().post(Entity.xml(userIdentityXml));
        return null;

    }

    @Override
    protected String getFallback() {
        return null;
    }

}
