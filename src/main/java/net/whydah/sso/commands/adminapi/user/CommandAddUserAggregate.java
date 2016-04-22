package net.whydah.sso.commands.adminapi.user;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.URI;

public class CommandAddUserAggregate extends BaseHttpPostHystrixCommand<String> {

    private String adminUserTokenId;
    private String userAggregateJson;

    public CommandAddUserAggregate(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userAggregateJson) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup");
      
        this.adminUserTokenId = adminUserTokenId;
        this.userAggregateJson = userAggregateJson;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userAggregateJson == null) {
            log.error(TAG + " initialized with null-values - will fail - userAdminServiceUri:{}, myAppTokenId:{}, adminUserTokenId:{}, userAggregateJson:{}", userAdminServiceUri, myAppTokenId, adminUserTokenId, userAggregateJson);
        }
    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + adminUserTokenId + "/useraggregate";
	}
	
	@Override
	protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
		return request.contentType("application/json").send(userAggregateJson);
		
	}

//    @Override
//    protected String run() {
//
//        log.trace("CommandAddUserAggregate - myAppTokenId={}, adminUserTokenId={{}, userAggregateJson={}", myAppTokenId, adminUserTokenId, userAggregateJson);
//        Client tokenServiceClient = ClientBuilder.newClient();
//        WebTarget addUser = tokenServiceClient.target(userAdminServiceUri).path(myAppTokenId).path(adminUserTokenId).path("useraggregate");
//        Response response = addUser.request().post(Entity.json(userAggregateJson));
//        if (response.getStatus() == OK.getStatusCode()) {
//            String responseXML = response.readEntity(String.class);
//            log.info("CommandAddUserAggregate - addUser - Create user OK with response {}", responseXML);
//            return responseXML;
//        }
//
//        log.warn("CommandAddUserAggregate - addUser - failed with status code " + response.getStatus());
//        return null;
//
//    }
//
//    @Override
//    protected String getFallback() {
//        log.warn("CommandAddUserAggregate - fallback - whydahServiceUri={}", userAdminServiceUri.toString());
//        return null;
//    }


}
