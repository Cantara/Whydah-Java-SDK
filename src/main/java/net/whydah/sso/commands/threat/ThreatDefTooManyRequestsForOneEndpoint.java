package net.whydah.sso.commands.threat;

import java.util.List;

public class ThreatDefTooManyRequestsForOneEndpoint extends IThreatDefinition {

	int COUNT = 1000000; //1 million requests in one hour?
	
	public int getCode() {
		return IThreatDefinition.DEF_CODE_MANY_REQUESTS_IN_A_SHORT_PERIOD;
	}

	
	public String getDesc() {
		return "detect if there are more than " + COUNT + " requests for one particular endpoint";
	}

	
	void detect(ThreatActivityLogCollector collector, ThreatObserver observer) {
	
		for(String endpoint : collector.getAllEndPoints()){
			List<ThreatActivityLog> logList = collector.getActivityLogByEndPoint(endpoint);
			if(logList.size()>= COUNT){
				ThreatSignalInfo info = new ThreatSignalInfo(this.getCode(), "", "", logList);
				observer.commitThreat(info);
			}
		}
	}

}
