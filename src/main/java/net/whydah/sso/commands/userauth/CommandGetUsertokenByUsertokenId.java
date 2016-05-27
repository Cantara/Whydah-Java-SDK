package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.util.ExceptionUtil;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


public class CommandGetUsertokenByUsertokenId extends BaseHttpPostHystrixCommand<String> {

   // private static final Logger log = LoggerFactory.getLogger(CommandGetUsertokenByUsertokenId.class);

    private String usertokenId;
    


    public CommandGetUsertokenByUsertokenId(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, String usertokenId) {
    	super(tokenServiceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", 6000);
        
        this.usertokenId = usertokenId;
        
        if (tokenServiceUri == null || myAppTokenId == null || myAppTokenXml == null || usertokenId == null) {
			log.error("CommandGetUsertokenByUsertokenId initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, myAppTokenXml:{}  usertokenId:{}", tokenServiceUri.toString(), myAppTokenId, myAppTokenXml, usertokenId);
		}

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
    		throw new RuntimeException(authenticationFailedMessage);
		}
	}

    
    
    @Override
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("apptoken", myAppTokenXml);
		data.put("usertokenid", usertokenId);
		return data;
	}
    
	@Override
	protected String getTargetPath() {

		return "user/" + myAppTokenId + "/get_usertoken_by_usertokenid";
	}


}