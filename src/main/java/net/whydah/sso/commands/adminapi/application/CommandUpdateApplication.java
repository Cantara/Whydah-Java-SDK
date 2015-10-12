package net.whydah.sso.commands.adminapi.application;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import net.whydah.sso.commands.adminapi.user.CommandAddUser;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

// TODO:  wait for https://github.com/Cantara/Whydah-UserAdminService/issues/35

public class CommandUpdateApplication extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandAddUser.class);
    private URI userAdminServiceUri;
    private String myAppTokenId;
    private String adminUserTokenId;
    private String applicationJson;


    public CommandUpdateApplication(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String applicationJson) {
        super(HystrixCommandGroupKey.Factory.asKey("UASUserAdminGroup"));
        this.userAdminServiceUri = userAdminServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.adminUserTokenId = adminUserTokenId;
        this.applicationJson = applicationJson;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || applicationJson == null) {
            log.error("CommandUpdateApplication initialized with null-values - will fail");
        }

    }

    @Override
    protected String run() {
        log.trace("CommandUpdateApplication - myAppTokenId={}", myAppTokenId);

        Client uasClient = ClientBuilder.newClient();

        WebTarget updateApplication = uasClient.target(userAdminServiceUri).path(myAppTokenId + "/" + adminUserTokenId + "/xxx");
        Response response = updateApplication.request().post(Entity.json(applicationJson));
        throw new UnsupportedOperationException();
        //return null;

    }

    @Override
    protected String getFallback() {
        log.warn("CommandUpdateApplication - timeout - uri={}", userAdminServiceUri.toString());
        return null;
    }


}
