package net.whydah.sso.commands.threat;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.whydah.sso.util.Lock;

import org.slf4j.Logger;

public class ThreatActivityLogCollector {
	
	private static final Logger logger = getLogger(ThreatActivityLogCollector.class);
	
	private Map<String, ThreatActivityLog> _threatLogCollection = new ConcurrentHashMap <>();
	private Map<String, LinkedList<String>> _sortByIPAddress = new ConcurrentHashMap <String, LinkedList<String>>();
	private Map<String, LinkedList<String>> _sortByEndpoint = new ConcurrentHashMap<String, LinkedList<String>>();
	
	Lock lock = new Lock();
	
	public List<ThreatActivityLog> getAll(){
		return new ArrayList<>(Collections.unmodifiableCollection(_threatLogCollection.values()));
	}
	
	public List<String> getAllEndPoints(){
		return new ArrayList<>(Collections.unmodifiableCollection(_sortByEndpoint.keySet()));
	}
	
	public List<String> getAllIpAddresses(){
		return new ArrayList<>(Collections.unmodifiableCollection(_sortByIPAddress.keySet()));
	}
	
	
	public List<ThreatActivityLog> getActivityLogByIPAddress(String ipAddress){
		List<ThreatActivityLog> result = new ArrayList<>();
		if(_sortByIPAddress.containsKey(ipAddress)){
			List<String> logIds = new ArrayList<>(Collections.unmodifiableList(_sortByIPAddress.get(ipAddress)));
			for(String logId : logIds){
				if(_threatLogCollection.containsKey(logId)){
					result.add(_threatLogCollection.get(logId));
				}
			}
		}
		return result;
	}
	
	public List<ThreatActivityLog> getActivityLogByEndPoint(String endpoint){
		List<ThreatActivityLog> result = new ArrayList<>();
		if(_sortByEndpoint.containsKey(endpoint)){
			List<String> logIds = new ArrayList<>(_sortByEndpoint.get(endpoint));
			for(String logId : logIds){
				if(_threatLogCollection.containsKey(logId)){
					result.add(_threatLogCollection.get(logId));
				}
			}
		}
		return result;
	}
	
	public void addLogForDetection(ThreatActivityLog log){
		while(_threatLogCollection.containsKey(log.getId())){
			log.setId(UUID.randomUUID().toString());
		}
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
	 * @return the RAW _threatLogCollection
	 */
	//USE WITH CARE
	public Map<String, ThreatActivityLog> get_AllLogCollection() {
		return _threatLogCollection;
	}


	/**
	 * @return the RAW _sortByIPAddress
	 */
	//USE WITH CARE
	public Map<String, LinkedList<String>> get_AllLogSortedByIPAddress() {
		return _sortByIPAddress;
	}



	/**
	 * @return the RAW _sortByEndpoint
	 */
	//USE WITH CARE
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
		
		if(!lock.isLocked()){
			try{
				lock.lock();
				logger.debug("start cleaning logs with (" + String.valueOf(REMOVAL_TIME_FOR_OLD_LOGS/(60000)) + ") minutes old");
				for(String uid : new ArrayList<String>(Collections.unmodifiableSet(_threatLogCollection.keySet()))){
					ThreatActivityLog log = _threatLogCollection.get(uid);
					if(System.currentTimeMillis() - Long.valueOf(log.getRequestTime())>= REMOVAL_TIME_FOR_OLD_LOGS){

						if(_sortByIPAddress.containsKey(log.getIpAddress())){
							_sortByIPAddress.get(log.getIpAddress()).remove(log.getId());
						}
						if(_sortByEndpoint.containsKey(log.getEndPoint())){
							_sortByIPAddress.get(log.getEndPoint()).remove(log.getId());
						}
						_threatLogCollection.remove(uid);
					}

				}}catch(Exception ex){
					ex.printStackTrace();
					logger.error("Error when cleaning old logs - " + ex.getMessage());
				} finally{
					lock.unlock();
				}
		}

	}


	
}
