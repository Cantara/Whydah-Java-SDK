package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.ddd.model.user.UserTokenId;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class CommandRefreshUserToken extends BaseHttpPostHystrixCommand<String>{

	public static int DEFAULT_TIMEOUT = 6000;
	String userTokenId;
	public CommandRefreshUserToken(URI serviceUri, String myAppTokenId,
			String myAppTokenXml, String userTokenId) {
		super(serviceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", DEFAULT_TIMEOUT);
        if (UserTokenId.isValid(userTokenId)) {
            this.userTokenId = userTokenId;
        }
    }
	
	public CommandRefreshUserToken(URI serviceUri, String myAppTokenId,
			String myAppTokenXml, String userTokenId, int timeout) {
		super(serviceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", timeout);
        if (UserTokenId.isValid(userTokenId)) {
            this.userTokenId = userTokenId;
        }
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