package net.whydah.sso.commands.threat;

import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;

public class ThreatObserverTest {
	 private static final Logger logger = getLogger(ThreatObserverTest.class);

	    
	    @Test
	    public void testRegisterDefinition()
	    {
	    	ThreatObserver ob = new ThreatObserver();
	    	
	    	
			//add 
			
			ob.registerDefinition(testingThreatDef);
			ob.registerDefinition(new ThreatDefManyLoginAttemptsFromSameIPAddress());
			ob.registerDefinition(new ThreatDefTooManyRequestsForOneEndpoint());
			
			assertTrue(ob.threatDefs.size()==3);
			
	    }
	    

	    @Test
	    public void testAddLogForDetectionWhenHavingNoThreatDefinition() {
	    	ThreatObserver ob = new ThreatObserver(null);
	    	//some login activity
	    	ThreatActivityLog log = new ThreatActivityLog().
	    	setEndPoint("login").setIpAddress("171.250.110.28").setRequestTime(Long.toString(System.currentTimeMillis()));
	    	
	    	ob.addLogForDetection(log);
	    	
	    	assertTrue(ob.collector.getAll().size()==1);
	    	
	    	ob.addLogForDetection(log);
	    	
	    	assertTrue(ob.collector.getAll().size()==2);
	    	
	    }
	    

	    public int suspicion = 0;
	    
	    IThreatDefinition testingThreatDef = new IThreatDefinition() {
			
			@Override
			public String getDesc() {
				//we can add this definition to the code base, but this must be tested 
				return "for testing purpose - time between each request from one IP address should not be so small";
			}
			
			@Override
			public int getCode() {
				return 99999;
			}
			
			
			public String convertTime(long time){
			    Date date = new Date(time);
			    Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
			    return format.format(date);
			}
			
			@Override
			void detect(ThreatActivityLogCollector collector, ThreatObserver observer) {
				
				for(String ip : collector.getAllIpAddresses()){
					
					List<ThreatActivityLog> logList = collector.getActivityLogByIPAddress(ip);
					
					//WE USED LINKED LIST
//					Collections.sort(logList, new Comparator<ThreatActivityLog>() {
//					    @Override
//					    public int compare(ThreatActivityLog o1, ThreatActivityLog o2) {
//					        return Long.valueOf(o1.getRequestTime()).compareTo(Long.valueOf(o2.getRequestTime()));
//					    }
//					});
					boolean found = false;
				    long previousTimeRequest = 0; 
				    int times = 0;
					for(ThreatActivityLog log : logList){
						
						
						long thisTimeRequest = Long.valueOf(log.getRequestTime());
						if(thisTimeRequest - previousTimeRequest <= 1000){
							times ++;
							found = true;
						}
						
						previousTimeRequest = Long.valueOf(log.getRequestTime());
						
					}
					
					if(found){
						suspicion ++;
						logger.info("This ip address " + ip + " is very suspicious, repeating " + times +" times");
						//commit anything we found, remove logs after we commit
						ThreatSignalInfo info = new ThreatSignalInfo(getCode(), ip, "repeating " + times + " times",collector.getActivityLogByIPAddress(ip));
						observer.commitThreat(info);
					}
					
					
					
				}
				
			}
		};
		
	    @Test
	    public void testAddLogForDetectionWhenHavingSomeThreatDefinitions() {
	    	ThreatObserver ob = new ThreatObserver();
	    	ob.registerDefinition(testingThreatDef);
	    	
	    	
	    	assertTrue(suspicion == 0);
	    	
	    	//some normal requests
	    	long now = System.currentTimeMillis();
	    	for(int i = 0; i < 10; i++){
	    		now = now + 3000;  //3 seconds each request
	    		ThreatActivityLog log = new ThreatActivityLog().setEndPoint("login").setIpAddress("171.250.110.28").setRequestTime(Long.toString(now));
	    		ob.addLogForDetection(log);
	    	}
	    	
	    	assertTrue(suspicion == 0);
	    	
	    	//some abnormal requests
	    	now = now + 5000;
	    	for(int i = 0; i < 10; i++){
	    		ThreatActivityLog log = new ThreatActivityLog().setEndPoint("login").setIpAddress("171.250.110.30").setRequestTime(Long.toString(now));
	    		log = log.setAdditionalProperty("usertokenid", "12368123912");
	    		ob.addLogForDetection(log);
	    		
	    		log = new ThreatActivityLog().setEndPoint("login").setIpAddress("171.250.110.31").setRequestTime(Long.toString(now));
	    		log = log.setAdditionalProperty("usertokenid", "dtdr345345345");
	    		ob.addLogForDetection(log);
	    		
	    		now = now + 300; //0.3 seconds each request
	    	}
	    	
	    	waitForAllDetectionsToFinish(ob);
	    	
	    	assertTrue(suspicion >= 2);
	    	
	    }


		private void waitForAllDetectionsToFinish(ThreatObserver ob) {
			while(!ob.isAllDetectionDone()){
	    		try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					
				} 
	    	}

	    	try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	    
	    
}
