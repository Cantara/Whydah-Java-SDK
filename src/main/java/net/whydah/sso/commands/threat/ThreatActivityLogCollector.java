package net.whydah.sso.commands.threat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ThreatActivityLogCollector {
	
	private Map<String, ThreatActivityLog> _threatLogCollection = new HashMap<>();
	private Map<String, LinkedList<String>> _sortByIPAddress = new HashMap<String, LinkedList<String>>();
	private Map<String, LinkedList<String>> _sortByEndpoint = new HashMap<String, LinkedList<String>>();
	
	public List<ThreatActivityLog> getAll(){
		return new ArrayList<>(_threatLogCollection.values());
	}
	
	public List<String> getAllEndPoints(){
		return new ArrayList<>(_sortByEndpoint.keySet());
	}
	
	public List<String> getAllIpAddresses(){
		return new ArrayList<>(_sortByIPAddress.keySet());
	}
	
	
	public List<ThreatActivityLog> getActivityLogByIPAddress(String ipAddress){
		List<String> logIds = new ArrayList<>(_sortByIPAddress.get(ipAddress));
		List<ThreatActivityLog> result = new ArrayList<>();
		for(String logId : logIds){
			if(_threatLogCollection.containsKey(logId)){
				result.add(_threatLogCollection.get(logId));
			}
		}
		return result;
	}
	
	public List<ThreatActivityLog> getActivityLogByEndPoint(String endpoint){
		List<String> logIds = new ArrayList<>(_sortByEndpoint.get(endpoint));
		List<ThreatActivityLog> result = new ArrayList<>();
		for(String logId : logIds){
			if(_threatLogCollection.containsKey(logId)){
				result.add(_threatLogCollection.get(logId));
			}
		}
		return result;
	}
	
	public void addLogForDetection(ThreatActivityLog log){
		_threatLogCollection.put(log.getId(), log);
		
		if(!_sortByIPAddress.containsKey(log.getIpAddress())){
			_sortByIPAddress.put(log.getIpAddress(), new LinkedList<>());
		}
		_sortByIPAddress.get(log.getIpAddress()).add(log.getId());
		
		if(!_sortByEndpoint.containsKey(log.getEndPoint())){
			_sortByEndpoint.put(log.getEndPoint(), new LinkedList<>());
		}
		_sortByEndpoint.get(log.getEndPoint()).add(log.getId());
	}


	/**
	 * @return the _threatLogCollection
	 */
	public Map<String, ThreatActivityLog> get_AllLogCollection() {
		return _threatLogCollection;
	}


	/**
	 * @return the _sortByIPAddress
	 */
	public Map<String, LinkedList<String>> get_AllLogSortedByIPAddress() {
		return _sortByIPAddress;
	}



	/**
	 * @return the _sortByEndpoint
	 */
	public Map<String, LinkedList<String>> get_AllLogSortedByEndpoint() {
		return _sortByEndpoint;
	}

	public void removeLogs(List<ThreatActivityLog> logList) {
		for(ThreatActivityLog log : logList){
			if(_sortByIPAddress.containsKey(log.getIpAddress())){
				_sortByIPAddress.remove(log.getId());
			}
			if(_sortByEndpoint.containsKey(log.getEndPoint())){
				_sortByEndpoint.remove(log.getId());
			}
			_threatLogCollection.remove(log.getId());
		}
	}

	public int REMOVAL_TIME_FOR_OLD_LOGS = 1000*60*60; //1 hour old
	public void cleanOldLogs() {
		for(String uid : new ArrayList<>(_threatLogCollection.keySet())){
			ThreatActivityLog log = new ThreatActivityLog();
			if(System.currentTimeMillis() - Long.valueOf(log.getRequestTime())>= REMOVAL_TIME_FOR_OLD_LOGS){

				if(_sortByIPAddress.containsKey(log.getIpAddress())){
					_sortByIPAddress.get(log.getIpAddress()).remove(log.getId());
				}
				if(_sortByEndpoint.containsKey(log.getEndPoint())){
					_sortByIPAddress.get(log.getEndPoint()).remove(log.getId());
				}
				_threatLogCollection.remove(uid);
			}
			
		}
		
	}


	
}
