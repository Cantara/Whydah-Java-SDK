package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommandForBooleanType;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.ddd.model.user.UserTokenId;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class CommandCreateTicketForUserTokenID extends BaseHttpPostHystrixCommandForBooleanType{

	String userTicket;
	String userTokenID;
	public static int DEFAULT_TIMEOUT = 6000;
	
	
	public CommandCreateTicketForUserTokenID(URI serviceUri,
                                             String applicationTokenId, String myAppTokenXml, String userTicket, String userTokenId) {
        super(serviceUri, myAppTokenXml, applicationTokenId, "SSOAUserAuthGroup", DEFAULT_TIMEOUT);

        if (serviceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(userTokenId)) {
            log.error(TAG + " initialized with null-values - will fail");
            log.error("CommandCreateTicketForUserTokenID initialized with null-values - will fail - crmServiceUri:{} myAppTokenId:{} userTokenId:{} personRef:{}", serviceUri, applicationTokenId, userTokenId);
        }

		this.userTicket = userTicket;
		this.userTokenID = userTokenId;
	}
	
	public CommandCreateTicketForUserTokenID(URI serviceUri,
            String applicationTokenId, String myAppTokenXml, String userTicket, String userTokenId, int timeout) {
super(serviceUri, myAppTokenXml, applicationTokenId, "SSOAUserAuthGroup", timeout);

if (serviceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(userTokenId)) {
log.error(TAG + " initialized with null-values - will fail");
log.error("CommandCreateTicketForUserTokenID initialized with null-values - will fail - crmServiceUri:{} myAppTokenId:{} userTokenId:{} personRef:{}", serviceUri, applicationTokenId, userTokenId);
}

this.userTicket = userTicket;
this.userTokenID = userTokenId;
}


	@Override
	protected String getTargetPath() {
		return "user/" + myAppTokenId  + "/create_userticket_by_usertokenid";
	}
	
	@Override
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("apptoken", myAppTokenXml);
		data.put("userticket", userTicket);
		data.put("usertokenid", userTokenID);
		return data;
	}
	
	@Override
	protected Boolean dealWithResponse(String response) {
		return true;
	}
	
	@Override
	protected Boolean dealWithFailedResponse(String responseBody, int statusCode) {
		return false;
	}
  
}
