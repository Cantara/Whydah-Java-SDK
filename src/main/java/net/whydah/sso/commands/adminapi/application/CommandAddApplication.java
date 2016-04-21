package net.whydah.sso.commands.adminapi.application;

import com.github.kevinsawicki.http.HttpRequest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import net.whydah.sso.commands.adminapi.user.CommandAddUser;
import net.whydah.sso.util.BaseHttpPostHystrixCommand;

import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

// TODO:  wait for https://github.com/Cantara/Whydah-UserAdminService/issues/35

public class CommandAddApplication extends BaseHttpPostHystrixCommand<String> {
    private static final Logger log = getLogger(CommandAddUser.class);
    
    private String adminUserTokenId;
    private String applicationJson;


    public CommandAddApplication(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String applicationJson) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup", 60000);
      
        this.adminUserTokenId = adminUserTokenId;
        this.applicationJson = applicationJson;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || applicationJson == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }

    }
    
    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
    	return request.contentType("application/json").send(applicationJson);
    }

//    @Override
//    protected String run() {
//        log.trace("CommandAddApplication - myAppTokenId={} - \n\n adding applicationJson={}", myAppTokenId, applicationJson);
//
//        Client tokenServiceClient = ClientBuilder.newClient();
//
//        WebTarget addApplication = tokenServiceClient.target(userAdminServiceUri).path(myAppTokenId + "/" + adminUserTokenId + "/application");
//        Response response = addApplication.request().post(Entity.json(applicationJson));
//        return response.getEntity().toString();
//    }


	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + adminUserTokenId + "/application";
	}

}
