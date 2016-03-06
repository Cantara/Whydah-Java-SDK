package net.whydah.sso.commands.extensions.statistics;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import net.whydah.sso.util.SSLTool;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.Instant;

import static javax.ws.rs.core.Response.Status.OK;
import static org.slf4j.LoggerFactory.getLogger;

public class CommandGetUsersStats extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandGetUsersStats.class);
    private final String prefix;
    private URI statisticsServiceUri;
    private String myAppTokenId;
    private String adminUserTokenId;
    private Instant startTime = null;
    private Instant endTime = null;


    public CommandGetUsersStats(URI statisticsServiceUri, String myAppTokenId, String adminUserTokenId, Instant startTime, Instant endTime) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("StatisticsExtensionGroup")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(3000)));

        this.statisticsServiceUri = statisticsServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.adminUserTokenId = adminUserTokenId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.prefix = "All";
//        if (statisticsServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userid == null) {
        if (statisticsServiceUri == null) {
            log.error("CommandGetUsersStats initialized with null-values - will fail");
        }

    }

    @Override
    protected String run() {
        log.trace("CommandGetUsersStats - myAppTokenId={}", myAppTokenId);

//        Client statisticsClient = ClientBuilder.newClient();
        Client statisticsClient;
        if (!SSLTool.isCertificateCheckDisabled()) {
            statisticsClient = ClientBuilder.newClient();
        } else {
            statisticsClient = ClientBuilder.newBuilder().sslContext(SSLTool.sc).hostnameVerifier((s1, s2) -> true).build();
        }


//        WebTarget updateUser = statisticsClient.target(statisticsServiceUri).path(myAppTokenId).path(adminUserTokenId).path("customer").path(userid);
        WebTarget findUserLogons = statisticsClient.target(statisticsServiceUri)
                .path("observe").path("statistics").path(prefix).path("userlogon");
        if (startTime != null){
            findUserLogons = findUserLogons.queryParam("startTime", startTime.toEpochMilli());
        }
        if (endTime != null) {
            findUserLogons = findUserLogons.queryParam("endTime", endTime.toEpochMilli());
        }
        Response response = findUserLogons.request().get();
        log.debug("CommandGetUsersStats - Returning list of user logons {}", response.getStatus());
        if (response.getStatus() == OK.getStatusCode()) {
            String responseJson = response.readEntity(String.class);
            log.debug("CommandGetUsersStats - Returning list of user logons  {}", responseJson);
            return responseJson;
        }
        String responseJson = response.readEntity(String.class);
        log.debug("CommandGetUsersStats - Returning list of user logons  {}", responseJson);
        return null;


    }

    @Override
    protected String getFallback() {
        log.warn("CommandGetUsersStats - fallback - uri={}", statisticsServiceUri.toString());
        return null;
    }


}