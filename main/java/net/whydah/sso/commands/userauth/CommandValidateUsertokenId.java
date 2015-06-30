package net.whydah.sso.commands.userauth;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.Response.Status.*;

/**
 * Created by totto on 12/2/14.
 */
public class CommandValidateUsertokenId extends HystrixCommand<Boolean> {

    private static final Logger log = LoggerFactory.getLogger(CommandValidateUsertokenId.class);

    private URI tokenServiceUri ;
    private String myAppTokenId ;
    private String usertokenid;



    public CommandValidateUsertokenId(URI tokenServiceUri, String myAppTokenId, String usertokenid) {
        super(HystrixCommandGroupKey.Factory.asKey("SSOUserAuthGroup"));
        this.tokenServiceUri = tokenServiceUri;
        this.myAppTokenId=myAppTokenId;
        this.usertokenid=usertokenid;
        if (tokenServiceUri == null || myAppTokenId == null || usertokenid == null  ) {
            log.error("CommandValidateUsertokenId initialized with null-values - will fail");
        }
    }

    @Override
    protected Boolean run() {

        log.trace("CommandValidateUsertokenId - myAppTokenId={}, userTokenID{}",myAppTokenId,usertokenid);

        Client tokenServiceClient = ClientBuilder.newClient();

// If we get strange values...  return false
        if (usertokenid == null || usertokenid.length() < 4) {
            log.warn("CommandValidateUsertokenId - verifyUserTokenId - Called with bogus usertokenid={}. return false", usertokenid);
            return false;
        }
        // logonApplication();
        WebTarget verifyResource = tokenServiceClient.target(tokenServiceUri).path("user/" + myAppTokenId + "/validate_usertokenid/" + usertokenid);
        Response response = get(verifyResource);
        if (response.getStatus() == OK.getStatusCode()) {
            log.info("CommandValidateUsertokenId - verifyUserTokenId - validate_usertokenid {}  result {}", "userauth/" + myAppTokenId + "/validate_usertokenid/" + usertokenid, response);
            return true;
        }
        if (response.getStatus() == CONFLICT.getStatusCode()) {
            log.warn("CommandValidateUsertokenId - verifyUserTokenId - usertokenid not ok: {}", response);
            return false;
        }
        //retry
        log.info("CommandValidateUsertokenId - verifyUserTokenId - retrying usertokenid ");
        //logonApplication();
        response = get(verifyResource);
        boolean bolRes = response.getStatus() == OK.getStatusCode();
        log.info("CommandValidateUsertokenId - verifyUserTokenId - validate_usertokenid {}  result {}", "user/" + myAppTokenId + "/validate_usertokenid/" + usertokenid, response);
        return bolRes;
    }

    private Response get(WebTarget verifyResource) {
        return verifyResource.request().get(Response.class);
    }

    @Override
    protected Boolean getFallback() {
        return false;
    }


}
