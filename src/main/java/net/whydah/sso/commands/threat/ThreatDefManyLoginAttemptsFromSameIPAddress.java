package net.whydah.sso.commands.threat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreatDefManyLoginAttemptsFromSameIPAddress extends IThreatDefTooManyRequestsForOneEndPointFromSameIPAddress {

	
	public ThreatDefManyLoginAttemptsFromSameIPAddress() {
		super("/action");
	}
	
	public ThreatDefManyLoginAttemptsFromSameIPAddress(int limit) {
		super(limit, "/action");
	}
	
	public int getCode() {
		return IThreatDefinition.DEF_CODE_MANY_LOGIN_ATTEMPTS;
	}

	public String getDesc() {
		return "detect if there are more than " + count  + " login attempts from the same IP-address";
	}

}
