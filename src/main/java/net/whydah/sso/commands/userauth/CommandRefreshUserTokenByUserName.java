package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.ddd.model.user.UserName;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class CommandRefreshUserTokenByUserName extends BaseHttpPostHystrixCommand<String>{

	public static int DEFAULT_TIMEOUT = 6000;
	String userName;
	public CommandRefreshUserTokenByUserName(URI serviceUri, String myAppTokenId,
			String myAppTokenXml, String userName) {
		super(serviceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", DEFAULT_TIMEOUT);
		if (UserName.isValid(userName)) {
			this.userName = userName;
		}
	}
	
	public CommandRefreshUserTokenByUserName(URI serviceUri, String myAppTokenId,
			String myAppTokenXml, String userName, int timeout) {
		super(serviceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", timeout);
		if (UserName.isValid(userName)) {
			this.userName = userName;
		}
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