package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class CommandRefreshUserToken extends BaseHttpPostHystrixCommand<String>{

	String userTokenId;
	public CommandRefreshUserToken(URI serviceUri, String myAppTokenId,
			String myAppTokenXml, String userTokenId) {
		super(serviceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", 3000);
		this.userTokenId = userTokenId; 
	}

	@Override
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("usertokenid", userTokenId);
		return data;
	}

	@Override
	protected String getTargetPath() {
		return "user/" + myAppTokenId+ "/refresh_usertoken";
	}

}