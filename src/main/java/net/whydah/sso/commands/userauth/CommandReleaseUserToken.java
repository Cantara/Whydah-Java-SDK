package net.whydah.sso.commands.userauth;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

//TODO:make test
public class CommandReleaseUserToken extends BaseHttpPostHystrixCommand<Boolean>{

	String userTokenId;
	public CommandReleaseUserToken(URI serviceUri, String myAppTokenId,
			String myAppTokenXml, String userTokenId) {
		super(serviceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup");
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
