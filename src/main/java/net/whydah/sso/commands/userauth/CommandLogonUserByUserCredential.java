package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.user.mappers.UserCredentialMapper;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.ExceptionUtil;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandLogonUserByUserCredential  extends BaseHttpPostHystrixCommand<String> {

    private UserCredential userCredential;
    private String userticket;
    public static int DEFAULT_TIMEOUT = 6000;

    public CommandLogonUserByUserCredential(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, UserCredential userCredential) {
        super(tokenServiceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", DEFAULT_TIMEOUT);
        this.userCredential=userCredential;
        this.userticket= UUID.randomUUID().toString();  // Create new UUID ticket if not provided
        if (tokenServiceUri == null || myAppTokenId == null || userCredential == null) {
            log.error("CommandLogonUserByUserCredential initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, myAppTokenXml:{}, userCredential:*****", tokenServiceUri, myAppTokenId, myAppTokenXml, userCredential);
        }
    }
    
    public CommandLogonUserByUserCredential(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, UserCredential userCredential, int timeout) {
        super(tokenServiceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", timeout);
        this.userCredential=userCredential;
        this.userticket= UUID.randomUUID().toString();  // Create new UUID ticket if not provided
        if (tokenServiceUri == null || myAppTokenId == null || userCredential == null) {
            log.error("CommandLogonUserByUserCredential initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, myAppTokenXml:{}, userCredential:*****", tokenServiceUri, myAppTokenId, myAppTokenXml, userCredential);
        }
    }

    public CommandLogonUserByUserCredential(URI tokenServiceUri,String myAppTokenId,String myAppTokenXml ,UserCredential userCredential,String userticket) {
        this(tokenServiceUri,myAppTokenId,myAppTokenXml,userCredential, DEFAULT_TIMEOUT);
        this.userticket=userticket;
    }

    public CommandLogonUserByUserCredential(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, String userCredentialXml, String userticket) {
        this(tokenServiceUri, myAppTokenId, myAppTokenXml, UserCredentialMapper.fromXml(userCredentialXml), DEFAULT_TIMEOUT);
        this.userticket = userticket;
    }

    public CommandLogonUserByUserCredential(URI tokenServiceUri,String myAppTokenId,String myAppTokenXml ,UserCredential userCredential,String userticket, int timeout) {
        this(tokenServiceUri,myAppTokenId,myAppTokenXml,userCredential, timeout);
        this.userticket=userticket;
    }

    public CommandLogonUserByUserCredential(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, String userCredentialXml, String userticket, int timeout) {
        this(tokenServiceUri, myAppTokenId, myAppTokenXml, UserCredentialMapper.fromXml(userCredentialXml), timeout);
        this.userticket = userticket;
    }


	@Override
	protected String getTargetPath() {
		return "user/" + myAppTokenId + "/" + userticket + "/usertoken";
	}


	@Override
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("apptoken", myAppTokenXml);
		data.put("usercredential", UserCredentialMapper.toXML(userCredential));
		return data;
	}

	int retryCnt=0;
	@Override
	protected String dealWithFailedResponse(String responseBody, int statusCode) {
		if(statusCode != java.net.HttpURLConnection.HTTP_FORBIDDEN &&retryCnt<1){
			//do retry
			retryCnt++;
			return doPostCommand();
		} else {
			String authenticationFailedMessage = ExceptionUtil.printableUrlErrorMessage("User session failed", request, statusCode);
    		log.warn(authenticationFailedMessage);
			return null;
		}
	}


}