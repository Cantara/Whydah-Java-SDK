package net.whydah.sso.commands.threat;

import java.util.List;

public class ThreatDefManyGetPinAttemptsFromSameIPAddress extends IThreatDefTooManyRequestsForOneEndPointFromSameIPAddress {

	public ThreatDefManyGetPinAttemptsFromSameIPAddress() {
		super("/getPin");
	}
	
	public ThreatDefManyGetPinAttemptsFromSameIPAddress(int limit) {
		super(limit, "/getPin");
	}
	
	public int getCode() {
		return IThreatDefinition.DEF_CODE_MANY_GET_PIN_ATTEMPTS;
	}

	public String getDesc() {
		return "detect if there are more than " + count  + " getPin attempts from the same IP-address";
	}


}
