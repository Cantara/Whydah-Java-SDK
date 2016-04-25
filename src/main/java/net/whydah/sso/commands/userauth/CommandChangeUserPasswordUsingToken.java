package net.whydah.sso.commands.userauth;

import java.net.URI;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import com.github.kevinsawicki.http.HttpRequest;

/**
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 2015-11-21.
 */
public class CommandChangeUserPasswordUsingToken extends BaseHttpPostHystrixCommand<String> {
    static final String CHANGE_PASSWORD_TOKEN_KEY = "changePasswordToken";
    //public static final String NEW_PASSWORD_KEY = "newpassword";
    
  
    
    private String uid;
    private String changePasswordToken;
    private String json;


    public CommandChangeUserPasswordUsingToken(String uibUri, String applicationtokenId, String uid, String changePasswordToken, String json) {
    	super(URI.create(uibUri),"", applicationtokenId,"SSOAUserAuthGroup");
       
        this.uid = uid;
        this.changePasswordToken = changePasswordToken;
        this.json = json;
        if (uibUri == null || applicationtokenId == null || uid == null || changePasswordToken == null || json == null) {
            log.error("{} initialized with null-values - will fail", CommandChangeUserPasswordUsingToken.class.getSimpleName());
        }
        if (uibUri == null || applicationtokenId == null || uid == null || changePasswordToken == null || json == null) {
            log.error("CommandGetUsertokenByUserticket initialized with null-values - will fail uibUri:{} myAppTokenId:{}, uid:{}...", uibUri, applicationtokenId, uid);
        }
    }

//    @Override
//    protected Response run() {
//        log.trace("{} - applicationtokenId={}, ", CommandChangeUserPasswordUsingToken.class.getSimpleName(), applicationtokenId);
//        Client client = ClientBuilder.newClient();
//        WebTarget uib = client.target(uasUrl);
//        WebTarget webResource = uib.path(applicationtokenId).path("user").path(uid).path("change_password");
//        return webResource.queryParam(CHANGE_PASSWORD_TOKEN_KEY, changePasswordToken).request().post(Entity.json(json));
//    }
//    
//    
//
//    @Override
//    protected Response getFallback() {
//        log.warn("{} - fallback - uasUrl={}", CommandChangeUserPasswordUsingToken.class.getSimpleName(), uasUrl);
//        return null;
//    }
    
    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
    	return request.contentType("application/json").send(json);
    }

    @Override
    protected Object[] getQueryParameters() {
    	return new String[]{CHANGE_PASSWORD_TOKEN_KEY, changePasswordToken};
    }
    
	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/user/" + uid + "/change_password";
	}
}
