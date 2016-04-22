package net.whydah.sso.commands.extensions.statistics;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;
import net.whydah.sso.util.SSLTool;

import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.net.URI;

import static javax.ws.rs.core.Response.Status.OK;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * https://whydahdev.cantara.no/reporter/observe/statistics/useradmin/usersession
 * https://whydahdev.cantara.no/reporter/observe/statistics/useradmin/usersession
 */
public class CommandListUserActivities extends BaseHttpGetHystrixCommand<String> {
    
    private final String prefix;
    private String userTokenId;
    private String userid;


    public CommandListUserActivities(URI statisticsServiceUri, String myAppTokenId, String userTokenId, String userid) {
    	super(statisticsServiceUri, "", myAppTokenId, "StatisticsExtensionGroup", 3000);
        
        this.userTokenId = userTokenId;
        this.userid = userid;
        this.prefix = "whydah";
//        if (statisticsServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userid == null) {
        if (statisticsServiceUri == null || userid == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }

    }

//    @Override
//    protected String run() {
//        log.trace("CommandListUserActivities - myAppTokenId={}", myAppTokenId);
//
////        Client statisticsClient = ClientBuilder.newClient();
//        Client statisticsClient;
//        if (!SSLTool.isCertificateCheckDisabled()) {
//            statisticsClient = ClientBuilder.newClient();
//        } else {
//            statisticsClient = ClientBuilder.newBuilder().sslContext(SSLTool.sc).hostnameVerifier((s1, s2) -> true).build();
//        }
//
//
////        WebTarget updateUser = statisticsClient.target(statisticsServiceUri).path(myAppTokenId).path(adminUserTokenId).path("customer").path(userid);
//        WebTarget findUserLogons = statisticsClient.target(statisticsServiceUri)
//                .path("observe").path("statistics").path(userid).path("usersession");
//        Response response = findUserLogons.request().get();
//        log.debug("CommandListUserActivities - Returning list of usersession {}", response.getStatus());
//        if (response.getStatus() == OK.getStatusCode()) {
//            String responseJson = response.readEntity(String.class);
//            log.debug("CommandListUserActivities - Returning list of usersession  {}", responseJson.substring(0, Math.min(responseJson.length(), 300)));  // max first 300 characters
//            return responseJson;
//        }
//        String responseJson = response.readEntity(String.class);
//        log.debug("CommandListUserActivities - Returning list of usersession  {}", responseJson);
//        return null;
//
//
//    }
//
//    @Override
//    protected String getFallback() {
//        log.warn("CommandListUserActivities - fallback - whydahServiceUri={}", statisticsServiceUri.toString());
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		return  "observe/statistics/" + userid + "/usersession";
	}


}
