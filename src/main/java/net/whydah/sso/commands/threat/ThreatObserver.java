package net.whydah.sso.commands.threat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.util.Lock;

public class ThreatObserver {

	List<IThreatDefinition> threatDefs = new ArrayList<>();
	ThreatActivityLogCollector collector = new ThreatActivityLogCollector();
	WhydahApplicationSession was = null;
	ExecutorService executor = Executors.newSingleThreadExecutor();
	ThreatObserver me = this;

	public ThreatObserver(){

	}

	public ThreatObserver(WhydahApplicationSession was){
		this.was = was;
	}

	public ThreatObserver(WhydahApplicationSession was, List<IThreatDefinition> threatDefinitions){
		this(was);
		this.threatDefs = threatDefinitions;
	}

	public void registerDefinition(IThreatDefinition definition){
		threatDefs.add(definition);
	}


	Lock lock = new Lock();
	boolean startAnotherDetection = false;
	public void addLogForDetection(ThreatActivityLog log){
		collector.addLogForDetection(log);
		if(!lock.isLocked()){
			try {
				lock.lock();
				executeDetection();
			} catch (InterruptedException e) {
				lock.unlock();
			}

		} else {
			startAnotherDetection = true;
		}

	}

	private void executeDetection() {
		executor.execute(new Runnable() {

			@Override
			public void run() {

				detect();

				if(startAnotherDetection){
					startAnotherDetection = false;
					detect();
				}
				

				lock.unlock();
				

			}
		});

	}

	private void detect() {

		System.out.println("INSPECTING ALL " + collector.get_AllLogCollection().size() + " REQUEST RECORDS");
		//clean up first, remove logs having 1 hour old request time
		collector.cleanOldLogs();
		//trigger detection
		for(IThreatDefinition def : threatDefs){
			def.triggerDetection(collector, me); 
		}

	}


	public boolean isAllDetectionDone(){
		for(IThreatDefinition def : threatDefs){
			if(def.lock.isLocked()){//there is a lock inside to make sure the trigger is fired only one time
				return false;
			}
		}
		return true;
	}

	//call back from IThreatDefinition.detect(...)
	public void commitThreat(ThreatSignalInfo info) {

		if(info.getThreatDefinitionCode() == IThreatDefinition.DEF_CODE_MANY_LOGIN_ATTEMPTS){
			//do something before reporting
		} else if(info.getThreatDefinitionCode() == IThreatDefinition.DEF_CODE_MANY_REQUESTS_IN_A_SHORT_PERIOD){
			//do something before reporting
		}

		if(was!=null){
			//TODO: some more info for ThreatSignal possibly
			this.was.reportThreatSignal(ThreatSignalInfo.toJson(info));
		} else {
			//log
			System.out.println("THREAT FOUND - " + ThreatSignalInfo.toJson(info));
		}

		//remove this log after reporting
		collector.removeLogs(info.getActivityLogList());


	}

}
