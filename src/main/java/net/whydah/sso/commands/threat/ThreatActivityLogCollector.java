package net.whydah.sso.commands.threat;

import net.whydah.sso.util.Lock;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;

public class ThreatActivityLogCollector {
	
	private static final Logger logger = getLogger(ThreatActivityLogCollector.class);
	
	protected final Map<String, ThreatActivityLog> _threatLogCollection; //item: log-unique-id and its log
	protected final Map<String, Long> _blackList; //item: suspect (phone number/ip-address) and the lastRequestTime
	
	final Lock lock = new Lock();
	
	public ThreatActivityLogCollector() {
		_threatLogCollection = new ConcurrentHashMap <>();
		_blackList = new ConcurrentHashMap<>();
	}
	
	
	public ThreatActivityLogCollector(Map<String, ThreatActivityLog> logs, Map<String, Long> blackList) {
		this._threatLogCollection = logs;
		this._blackList = blackList;
	}
	
	public List<ThreatActivityLog> getAll(){
		return new ArrayList<>(Collections.unmodifiableCollection(_threatLogCollection.values()));
	}
	
	public List<String> getAllEndPoints(){
		List<String> result = new ArrayList<>();
		for(String id : new ArrayList<>(_threatLogCollection.keySet())) {
			String endPoint = _threatLogCollection.get(id).getEndPoint().toLowerCase();
			if(!result.contains(endPoint)) {
				result.add(endPoint);
			}
		}
		return result;
	}
	
	public List<String> getAllIpAddresses(){
		List<String> result = new ArrayList<>();
		for(String id : new ArrayList<>(_threatLogCollection.keySet())) {
			String ip = _threatLogCollection.get(id).getIpAddress().toLowerCase();
			if(!result.contains(ip)) {
				result.add(ip);
			}
		}
		return result;
	}
	
	
	public List<ThreatActivityLog> getActivityLogByIPAddress(String ipAddress){
		List<ThreatActivityLog> result = new ArrayList<>();
		for(String id : new ArrayList<>(_threatLogCollection.keySet())) {
			if(_threatLogCollection.get(id).getIpAddress().equalsIgnoreCase(ipAddress)) {
				result.add(_threatLogCollection.get(id));
			}
		}
		Collections.sort(result, new Comparator<ThreatActivityLog>() {

			@Override
			public int compare(ThreatActivityLog t1, ThreatActivityLog t2) {
				
				long rt1 = Long.parseLong(t1.getRequestTime());
				long rt2 =  Long.parseLong(t2.getRequestTime());
				return rt1<rt2?-1:(rt1>rt2)?1:0;
			}
			
		});
		return result;
	}
	
	public List<ThreatActivityLog> getActivityLogByEndPoint(String endpoint){
		List<ThreatActivityLog> result = new ArrayList<>();
		for(String id : new ArrayList<>(_threatLogCollection.keySet())) {
			
			    endpoint = endpoint.replaceAll("\\*", "(.*?)");
	            Pattern pattern = Pattern.compile(endpoint, Pattern.CASE_INSENSITIVE);
	            Matcher matcher = pattern.matcher(_threatLogCollection.get(id).getEndPoint());
	            if (matcher.find()) {
	            	result.add(_threatLogCollection.get(id));
	            }
	            /*
			
			if(_threatLogCollection.get(id).getEndPoint().equalsIgnoreCase(endpoint)) {
				result.add(_threatLogCollection.get(id));
			} else {
				if(endpoint.contains("*")) {
		            endpoint = endpoint.replaceAll("\\*", "(.*?)");
		            Pattern pattern = Pattern.compile(endpoint, Pattern.CASE_INSENSITIVE);
		            Matcher matcher = pattern.matcher(_threatLogCollection.get(id).getEndPoint());
		            if (matcher.find()) {
		            	result.add(_threatLogCollection.get(id));
		            }
				}
		        
			}*/
			
			
		}
		Collections.sort(result, new Comparator<ThreatActivityLog>() {

			@Override
			public int compare(ThreatActivityLog t1, ThreatActivityLog t2) {
				
				long rt1 = Long.parseLong(t1.getRequestTime());
				long rt2 =  Long.parseLong(t2.getRequestTime());
				return rt1>rt2?-1:(rt1<rt2)?1:0;
			}
			
		});
		return result;
	}
	
	public void addLogForDetection(ThreatActivityLog log){
		logger.debug("Log added for detection: " + ThreatActivityLog.toJson(log));
		
		while(_threatLogCollection.containsKey(log.getId())){
			log.setId(UUID.randomUUID().toString());
		}
		_threatLogCollection.put(log.getId(), log);
	}


	/**
	 * @return the RAW _threatLogCollection
	 */
	//USE WITH CARE
	public Map<String, ThreatActivityLog> get_AllLogCollection() {
		return _threatLogCollection;
	}


	public void removeLogs(List<ThreatActivityLog> logList) {
		for(ThreatActivityLog log : logList){
			_threatLogCollection.remove(log.getId());
		}
	}

	public int REMOVAL_TIME_FOR_OLD_LOGS = 1000*60*60; //1 hour old
	public void cleanOldThreats() {
		
		if(!lock.isLocked()){
			try{
				lock.lock();
				logger.debug("start cleaning logs with (" + String.valueOf(REMOVAL_TIME_FOR_OLD_LOGS/(60000)) + ") minutes old");
				for(String uid : new ArrayList<String>(Collections.unmodifiableSet(_threatLogCollection.keySet()))){
					ThreatActivityLog log = _threatLogCollection.get(uid);
					if(System.currentTimeMillis() - Long.valueOf(log.getRequestTime())>= REMOVAL_TIME_FOR_OLD_LOGS){
						_threatLogCollection.remove(uid);
					}
				}
				
				for(String k: new ArrayList<String>(Collections.unmodifiableSet(_blackList.keySet()))) {
					if(System.currentTimeMillis() - Long.valueOf(_blackList.get(k))>= REMOVAL_TIME_FOR_OLD_LOGS){
						_blackList.remove(k);
					}
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
				logger.error("Error when cleaning old logs - " + ex.getMessage());
			} finally{
				lock.unlock();
			}
		}

	}


	
	public void addToBlackList(ThreatSignalInfo info) {
		if(info.getSuspect()==null||info.getSuspect().isEmpty()) {
			return;
		}
		logger.warn("Threat found and added to blacklist: {}", ThreatSignalInfo.toJson(info) );
		_blackList.put(info.getSuspect(), info.getActivityLogList().size()>0? Long.parseLong(info.getActivityLogList().get(info.getActivityLogList().size()-1).getRequestTime()): System.currentTimeMillis());
	}


	
}
