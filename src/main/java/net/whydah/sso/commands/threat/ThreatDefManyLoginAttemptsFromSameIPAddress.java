package net.whydah.sso.commands.threat;

import java.util.List;

public class ThreatDefManyLoginAttemptsFromSameIPAddress extends IThreatDefinition {


	public int getCode() {
		return IThreatDefinition.DEF_CODE_MANY_LOGIN_ATTEMPTS;
	}

	public String getDesc() {
		return "detect if there are more than 20+ signon attempts from the same IP-address";
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
				}
				if(i>=20){
					//commit all the logList for observing unusual behavior
					ThreatSignalInfo info = new ThreatSignalInfo(this.getCode(), ipaddress, "" ,logList);
					observer.commitThreat(info);
				}
			}
		}
	}



}
