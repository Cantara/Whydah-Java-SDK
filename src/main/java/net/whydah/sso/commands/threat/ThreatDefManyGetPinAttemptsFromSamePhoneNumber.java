package net.whydah.sso.commands.threat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreatDefManyGetPinAttemptsFromSamePhoneNumber extends IThreatDefinition {

	int count = 20;

	public ThreatDefManyGetPinAttemptsFromSamePhoneNumber() {
		
	}
	
	public ThreatDefManyGetPinAttemptsFromSamePhoneNumber(int limit) {
		count = limit;
	}
	
	public int getCode() {
		return IThreatDefinition.DEF_CODE_MANY_geT_PIN_ATTEMPTS_PER_PHONENUMBER;
	}

	public String getDesc() {
		return "detect if there are more than " + count  + " getPin attempts from the same phone number";
	}


	void detect(ThreatActivityLogCollector collector, ThreatObserver observer) {

		detectEndpoint(collector, observer, "/opplysningen_lookup");
		detectEndpoint(collector, observer, "/getPin");
		detectEndpoint(collector, observer, "/*/api/getPin/*");
	}

	private void detectEndpoint(ThreatActivityLogCollector collector, ThreatObserver observer, String endpoint) {
		List<ThreatActivityLog> logList = collector.getActivityLogByEndPoint(endpoint);
		Map<String, Integer> numberOfAtemptsPerPhone = new HashMap<>(); 
		Map<String, List<ThreatActivityLog>> logsPerPhone = new HashMap<>(); 
		for(ThreatActivityLog log : logList){
			if(log.getAdditionalProperties().containsKey("phoneNo")) {
				String phoneNumber = String.valueOf(log.getAdditionalProperties().get("phoneNo"));
				if(!numberOfAtemptsPerPhone.containsKey(phoneNumber)) {
					numberOfAtemptsPerPhone.put(phoneNumber, 1);
					logsPerPhone.put(phoneNumber, new ArrayList<>());
				} else {
					int num = numberOfAtemptsPerPhone.get(phoneNumber);
					numberOfAtemptsPerPhone.put(phoneNumber, ++num);
				}
				logsPerPhone.get(phoneNumber).add(log);
			}		
		}
		
		for(String phoneNumber : numberOfAtemptsPerPhone.keySet()) {
			if(numberOfAtemptsPerPhone.get(phoneNumber) >= count) {
				ThreatSignalInfo info = new ThreatSignalInfo(this.getCode(), phoneNumber, "" , logsPerPhone.get(phoneNumber));
				observer.commitThreat(info);
			}
		}
	}



}
