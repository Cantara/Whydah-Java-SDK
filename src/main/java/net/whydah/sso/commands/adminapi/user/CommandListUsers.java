package net.whydah.sso.commands.adminapi.user;

import com.github.kevinsawicki.http.HttpRequest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import net.whydah.sso.util.HttpSender;
import org.slf4j.Logger;

import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;


public class CommandListUsers extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandListUsers.class);
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
        String uasAdminApiUrl = userAdminServiceUri.toString() + myAppTokenId + "/" + adminUserTokenId + "/users/find/" + userQuery;

        HttpRequest request = HttpRequest.get(uasAdminApiUrl);
        int statusCode = request.code();
        String responseBody = request.body();
        switch (statusCode) {
            case HttpSender.STATUS_OK:
                log.debug("CommandListUsers - {}", responseBody);
                return responseBody;
            default:
                log.warn("Unexpected response from UAS. Response is {} content is {}", responseBody, responseBody);

        }
        throw new RuntimeException("CommandListUsers - Operation failed");


    }


    @Override
    protected String getFallback() {
        log.warn("CommandListUsers - fallback - uri={}", userAdminServiceUri.toString());
        return null;
    }


}
