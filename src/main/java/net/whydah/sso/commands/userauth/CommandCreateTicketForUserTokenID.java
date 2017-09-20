package net.whydah.sso.commands.userauth;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommandForBooleanType;

public class CommandCreateTicketForUserTokenID extends BaseHttpPostHystrixCommandForBooleanType{

	String userTicket;
	String userTokenID;
	
	public CommandCreateTicketForUserTokenID(URI serviceUri,
			String myAppTokenId, String myAppTokenXml, String userTicket, String userTokenId) {
		super(serviceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup");
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
