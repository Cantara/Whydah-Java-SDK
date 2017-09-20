package net.whydah.sso.commands.threat;

import java.util.List;

public class ThreatDefManyLoginAttemptsFromSameIPAddress extends IThreatDefinition {

	int count = 20;

	public int getCode() {
		return IThreatDefinition.DEF_CODE_MANY_LOGIN_ATTEMPTS;
	}

	public String getDesc() {
		return "detect if there are more than " + count  + " signon attempts from the same IP-address";
	}


	void detect(ThreatActivityLogCollector collector, ThreatObserver observer) {

		for(String ipaddress : collector.getAllIpAddresses()){

			if(ipaddress!=null){
				int i = 0; 
				List<ThreatActivityLog> logList = collector.getActivityLogByIPAddress(ipaddress);
			
				for(ThreatActivityLog log : logList){
					if(log.getEndPoint().toLowerCase().equals("login")){
						i++;
						
					}
					//there is no need inspecting anymore b/c suspects has been found. We just commit all logs to STS
					if(i>=count){
						break;
					}
				}
				
				if(i>=count){
					ThreatSignalInfo info = new ThreatSignalInfo(this.getCode(), ipaddress, "" , logList);
					observer.commitThreat(info);
				}
			}
		}
	}



}
