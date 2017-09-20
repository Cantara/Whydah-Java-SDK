package net.whydah.sso.commands.userauth;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.util.ExceptionUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandGetUsertokenByUserticket extends BaseHttpPostHystrixCommand<String> {

    private static final Logger log = LoggerFactory.getLogger(CommandGetUsertokenByUserticket.class);

    
    private String userticket;
    int retryCnt = 0;

    public CommandGetUsertokenByUserticket(URI tokenServiceUri,String myAppTokenId,String myAppTokenXml,String userticket) {
        super(tokenServiceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", 1500);
        this.userticket=userticket;
        
        if (tokenServiceUri == null || myAppTokenId == null || myAppTokenXml == null || userticket == null) {
            log.error(TAG + " initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, myAppTokenXml:{}, userCredential:*****", tokenServiceUri, myAppTokenId, myAppTokenXml, userticket);
        }

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