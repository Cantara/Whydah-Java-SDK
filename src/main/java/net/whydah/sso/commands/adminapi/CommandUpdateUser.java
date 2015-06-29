package net.whydah.sso.commands.adminapi;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.slf4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by totto on 24.06.15.
 */
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

        Client tokenServiceClient = ClientBuilder.newClient();

        WebTarget addUser = tokenServiceClient.target(userAdminServiceUri).path(myAppTokenId + "/" + adminUserTokenId + "/xxx");
        // Response response = addUser.request().post(Entity.xml(userIdentityXml));
        throw new NotImplementedException();
        //return null;

    }

}
