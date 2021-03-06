package net.whydah.sso.commands.userauth;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommandForBooleanType;

public class CommandReleaseUserToken extends BaseHttpPostHystrixCommandForBooleanType{

	public static int DEFAULT_TIMEOUT = 6000;
	String userTokenId;
	public CommandReleaseUserToken(URI serviceUri, String myAppTokenId,
			String myAppTokenXml, String userTokenId) {
		super(serviceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", DEFAULT_TIMEOUT);
		this.userTokenId = userTokenId; 
	}
	
	public CommandReleaseUserToken(URI serviceUri, String myAppTokenId,
			String myAppTokenXml, String userTokenId, int timeout) {
		super(serviceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", timeout);
		this.userTokenId = userTokenId; 
	}

	@Override
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("usertokenid", userTokenId);
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

	@Override
	protected String getTargetPath() {
		// TODO Auto-generated method stub
		return "user/" + myAppTokenId+ "/release_usertoken";
	}

}
