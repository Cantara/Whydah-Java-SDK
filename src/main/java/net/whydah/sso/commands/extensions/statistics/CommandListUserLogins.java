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

public class CommandListUserLogins extends BaseHttpGetHystrixCommand<String> {
    
    private final String prefix;
    private String adminUserTokenId;
    private String userid;


    public CommandListUserLogins(URI statisticsServiceUri, String myAppTokenId, String adminUserTokenId, String userid) {
    	super(statisticsServiceUri, "", myAppTokenId, "StatisticsExtensionGroup", 3000);
        
        this.adminUserTokenId = adminUserTokenId;
        this.userid = userid;
        this.prefix = "whydah";
//        if (statisticsServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userid == null) {
        if (statisticsServiceUri == null || userid == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }

    }

//    @Override
//    protected String run() {
//        log.trace("CommandListUserLogins - myAppTokenId={}", myAppTokenId);
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
//                .path("observe").path("activities").path(prefix).path("logon").path("user").path(userid);
//        Response response = findUserLogons.request().get();
//        log.debug("CommandListUserLogins - Returning list of user logons {}", response.getStatus());
//        if (response.getStatus() == OK.getStatusCode()) {
//            String responseJson = response.readEntity(String.class);
//            log.debug("CommandListUserLogins - Returning list of user logons  {}", responseJson);
//            return responseJson;
//        }
//        String responseJson = response.readEntity(String.class);
//        log.debug("CommandListUserLogins - Returning list of user logons  {}", responseJson);
//        return null;
//
//
//    }
//
//    @Override
//    protected String getFallback() {
//        log.warn("CommandListUserLogins - fallback - whydahServiceUri={}", statisticsServiceUri.toString());
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		return "observe/activities/"+ prefix + "/logon/user/" + userid;
	}


}
