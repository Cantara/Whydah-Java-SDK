package net.whydah.sso.commands.threat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IThreatDefTooManyRequestsForOneEndPointFromSameIPAddress extends IThreatDefinition {

	int count = 20;
	String endPoint = null;
	public IThreatDefTooManyRequestsForOneEndPointFromSameIPAddress(int limit, String endpoint) {
		count = limit;
		this.endPoint = endpoint;
		
	}
	
	public IThreatDefTooManyRequestsForOneEndPointFromSameIPAddress(String endpoint) {
		this.endPoint = endpoint;
	}

	void detect(ThreatActivityLogCollector collector, ThreatObserver observer) {
		if(endPoint!=null && !endPoint.isEmpty()) {
			List<ThreatActivityLog> logList = collector.getActivityLogByEndPoint(endPoint.startsWith("/")?endPoint:"/" + endPoint);
			Map<String, List<ThreatActivityLog>> logsPerIPAddress = new HashMap<>(); 
			for(ThreatActivityLog log : logList){
				if(!logsPerIPAddress.containsKey(log.getIpAddress())) {
					logsPerIPAddress.put(log.getIpAddress(), new ArrayList<>());
				}
				logsPerIPAddress.get(log.getIpAddress()).add(log);		
			}

			for(String ipAddress : logsPerIPAddress.keySet()) {
				if(logsPerIPAddress.get(ipAddress).size() >= count) {
					ThreatSignalInfo info = new ThreatSignalInfo(this.getCode(), ipAddress, "" , logsPerIPAddress.get(ipAddress));
					observer.commitThreat(info);
				}
			}
		}
	}



}
