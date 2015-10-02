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
public class CommandUpdateUserRole extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandAddUser.class);
    private URI userAdminServiceUri;
    private String myAppTokenId;
    private String adminUserTokenId;
    private String userRoleJson;


    public CommandUpdateUserRole(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userRoleJson) {
        super(HystrixCommandGroupKey.Factory.asKey("UASUserAdminGroup"));
        this.userAdminServiceUri = userAdminServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.adminUserTokenId = adminUserTokenId;
        this.userRoleJson = userRoleJson;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userRoleJson == null) {
            log.error("CommandUpdateUserRole initialized with null-values - will fail");
        }

    }

    @Override
    protected String run() {
        log.trace("CommandUpdateUserRole - myAppTokenId={}", myAppTokenId);

        Client tokenServiceClient = ClientBuilder.newClient();

        WebTarget addUser = tokenServiceClient.target(userAdminServiceUri).path(myAppTokenId + "/" + adminUserTokenId + "/xxx");
        // Response response = addUser.request().post(Entity.xml(userIdentityXml));
        throw new UnsupportedOperationException();
        //return null;

    }

    @Override
    protected String getFallback() {
        log.warn("CommandUpdateUserRole - timeout");
        return null;
    }


}
