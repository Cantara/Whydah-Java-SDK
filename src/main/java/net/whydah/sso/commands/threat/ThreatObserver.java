package net.whydah.sso.commands.threat;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.util.Lock;

import org.slf4j.Logger;

//observer for each application
public class ThreatObserver {

	private static final Logger logger = getLogger(ThreatObserver.class);
	
	List<IThreatDefinition> threatDefs = new ArrayList<>();
	ThreatActivityLogCollector collector = new ThreatActivityLogCollector();
	WhydahApplicationSession was = null;
	ThreatObserver me = this;
	public static int LOGS_CHECK_INTERVAL = 5;
	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> sf = scheduler.scheduleAtFixedRate(
             new Runnable() {
                 public void run() {
                	 try {
                		
                		detect();
                		
                	 } catch(Exception ex) {
                		 logger.error("Detection process failed! - message: " + ex.getMessage());
                		 ex.printStackTrace();
                	 }
                 }
             },
             1, LOGS_CHECK_INTERVAL, TimeUnit.SECONDS);
    
	public ThreatObserver(){
		
	}
	
	public ThreatObserver(WhydahApplicationSession was){
		this.was = was;
	}
	
	public ThreatObserver(WhydahApplicationSession was, Map<String, ThreatActivityLog> logsRepository, Map<String, Long> blackList){
		this.was = was;
		this.collector = new ThreatActivityLogCollector(logsRepository, blackList);
	}
	
	public ThreatObserver(WhydahApplicationSession was, int time_to_remove_or_block_threats_in_milliseconds){
		this.was = was;
		this.collector.REMOVAL_TIME_FOR_OLD_LOGS = time_to_remove_or_block_threats_in_milliseconds;
	}
	
	public ThreatObserver(WhydahApplicationSession was, Map<String, ThreatActivityLog> logsRepository, Map<String, Long> blackList, int time_to_remove_or_block_threats_in_milliseconds){
		this.was = was;
		this.collector = new ThreatActivityLogCollector(logsRepository, blackList);
		this.collector.REMOVAL_TIME_FOR_OLD_LOGS = time_to_remove_or_block_threats_in_milliseconds;
	}
	
	public ThreatObserver(WhydahApplicationSession was, List<IThreatDefinition> threatDefinitions){
		this.was = was;
		this.threatDefs = threatDefinitions;
	}
	
	public ThreatObserver(WhydahApplicationSession was, List<IThreatDefinition> threatDefinitions, Map<String, ThreatActivityLog> logsRepository, Map<String, Long> blackList){
		this.was = was;
		this.threatDefs = threatDefinitions;
		this.collector = new ThreatActivityLogCollector(logsRepository, blackList);
	}
	
	public ThreatObserver(WhydahApplicationSession was, List<IThreatDefinition> threatDefinitions, int time_to_remove_or_block_threats_in_milliseconds){
		this.was = was;
		this.threatDefs = threatDefinitions;
		this.collector.REMOVAL_TIME_FOR_OLD_LOGS = time_to_remove_or_block_threats_in_milliseconds;
	}
	
	public ThreatObserver(WhydahApplicationSession was, List<IThreatDefinition> threatDefinitions, Map<String, ThreatActivityLog> logsRepository, Map<String, Long> blackList, int time_to_remove_or_block_threats_in_milliseconds){
		this.was = was;
		this.threatDefs = threatDefinitions;
		this.collector = new ThreatActivityLogCollector(logsRepository, blackList);
		this.collector.REMOVAL_TIME_FOR_OLD_LOGS = time_to_remove_or_block_threats_in_milliseconds;
	}
	

	public void registerDefinition(IThreatDefinition definition){
		threatDefs.add(definition);
	}


	Lock lock = new Lock();

	public void addLogForDetection(ThreatActivityLog log){
		collector.addLogForDetection(log);
	}


	private void detect() {
		if(lock.isLocked()) {
			return;
		}
		try {
			lock.lock();
			long start = System.currentTimeMillis();
			logger.debug("detecting  " + collector.get_AllLogCollection().size() + " request records");
			//clean up first, remove logs having 1 hour old request time
			collector.cleanOldThreats();
			//trigger detection
			for(IThreatDefinition def : threatDefs){
				def.triggerDetection(collector, me); 
			}

			logger.debug("detection done in " + String.valueOf(((System.currentTimeMillis() - start)/1000)) + " seconds");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

	}


	public boolean isDetectionDone(){
	
		for(IThreatDefinition def : threatDefs){
			if(def.isDetecting()){//there is a lock inside to make sure the trigger is fired only one time
				return false;
			}
		}
		return true;
	}

	//call back from IThreatDefinition.detect(...)
	public void commitThreat(ThreatSignalInfo info) {

		//set appId if any. This should be assigned when running in real time
		if(this.was!=null) {
			info.setAppId(this.was.getMyApplicationCredential().getApplicationID());
		}
		
		List<ThreatActivityLog> origin = info.getActivityLogList();
		int limit = Math.min(500, info.getActivityLogList().size());
		//copy maximum 500 records only, no need to commit everything
		int fromIndex = info.getActivityLogList().size() - limit;
		List<ThreatActivityLog> subList = origin.subList(fromIndex, info.getActivityLogList().size()-1);
		info.setActivityLogList(subList);

		logger.info("THREAT FOUND - " + ThreatSignalInfo.toJson(info));
		
		//add the suspect to our blacklist
		this.addToBlackList(info);
		
		//commit to STS
		if(was!=null){
			//TODO: some more info for ThreatSignal possibly
			this.was.reportThreatSignal(ThreatSignalInfo.toJson(info));
		}

		//remove this log after reporting
		collector.removeLogs(origin);


	}

	public void addToBlackList(ThreatSignalInfo info) {
		
		if(was!=null) {
			if(!this.was.isWhiteListed(info.getSuspect())) {
				this.collector.addToBlackList(info);
			}
		} else {
			this.collector.addToBlackList(info);
		}
	}
	
	public boolean isBlackListed(String identity){
		return this.collector._blackList.containsKey(identity);
	}
}
