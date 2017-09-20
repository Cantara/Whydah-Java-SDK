package net.whydah.sso.internal.commands.uib.userauth;

import java.net.URI;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import com.github.kevinsawicki.http.HttpRequest;

public class CommandChangeUserPasswordUsingToken extends BaseHttpPostHystrixCommand<String> {
    static final String CHANGE_USERCRED_TOKEN_KEY = "changePasswordToken";

  
    
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
    }


    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
    	return request.contentType("application/json").send(json);
    }

    @Override
    protected Object[] getQueryParameters() {
        return new String[]{CHANGE_USERCRED_TOKEN_KEY, changePasswordToken};
    }
    
	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/user/" + uid + "/change_password";
	}
}
