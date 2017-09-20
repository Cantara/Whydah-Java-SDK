package net.whydah.sso.commands.userauth;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

public class CommandRefreshUserTokenByUserName extends BaseHttpPostHystrixCommand<String>{

	String userName;
	public CommandRefreshUserTokenByUserName(URI serviceUri, String myAppTokenId,
			String myAppTokenXml, String userName) {
		super(serviceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", 3000);
		this.userName = userName; 
	}

	@Override
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("username", userName);
		return data;
	}

	@Override
	protected String getTargetPath() {
		return "user/" + myAppTokenId+ "/refresh_usertoken_by_username";
	}

}