package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class CommandGetUserTokenByUserTicket extends BaseHttpPostHystrixCommand<String> {

    private static final Logger log = LoggerFactory.getLogger(CommandGetUserTokenByUserTicket.class);

    public static int DEFAULT_TIMEOUT = 6000;
    private String userticket;
    int retryCnt = 0;

    public CommandGetUserTokenByUserTicket(URI tokenServiceUri, String applicationTokenId, String myAppTokenXml, String userticket) {
        super(tokenServiceUri, myAppTokenXml, applicationTokenId, "SSOAUserAuthGroup", DEFAULT_TIMEOUT);


        if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || myAppTokenXml == null || userticket == null) {
            log.error(TAG + " initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, myAppTokenXml:{}, userCredential:*****", tokenServiceUri, applicationTokenId, myAppTokenXml, userticket);
        }
        this.userticket = userticket;

    }

    public CommandGetUserTokenByUserTicket(URI tokenServiceUri, String applicationTokenId, String myAppTokenXml, String userticket, int timeout) {
        super(tokenServiceUri, myAppTokenXml, applicationTokenId, "SSOAUserAuthGroup", timeout);


        if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || myAppTokenXml == null || userticket == null) {
            log.error(TAG + " initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, myAppTokenXml:{}, userCredential:*****", tokenServiceUri, applicationTokenId, myAppTokenXml, userticket);
        }
        this.userticket = userticket;

    }


    @Override
    protected String dealWithFailedResponse(String responseBody, int statusCode) {
    	if (statusCode == java.net.HttpURLConnection.HTTP_FORBIDDEN) {
            log.warn("CommandGetUsertokenByUserticket - getUserTokenByUserTicket failed");
            throw new IllegalArgumentException(TAG + " - getUserTokenByUserTicket failed.");
        } else if (retryCnt<1){
        	retryCnt++;
        	return doPostCommand();
        } else {
        	String authenticationFailedMessage = ExceptionUtil.printableUrlErrorMessage("User authentication failed", request, statusCode);
        	log.warn(authenticationFailedMessage);
        	throw new RuntimeException(authenticationFailedMessage);
        	
        }
    }

	@Override
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("apptoken", myAppTokenXml);
		data.put("userticket", userticket);
		return data;
	}
    
	@Override
	protected String getTargetPath() {
		return "user/" + myAppTokenId + "/get_usertoken_by_userticket";
	}


}