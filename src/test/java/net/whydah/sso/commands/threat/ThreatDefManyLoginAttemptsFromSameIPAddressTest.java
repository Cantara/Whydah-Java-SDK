package net.whydah.sso.commands.threat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import org.junit.Test;
import org.slf4j.Logger;

public class ThreatDefManyLoginAttemptsFromSameIPAddressTest {
	 private static final Logger logger = getLogger(ThreatDefManyLoginAttemptsFromSameIPAddressTest.class);
	 boolean found = false;
	 
	 @Test
	 public void testOperation() throws InterruptedException
	 {
		 ThreatObserver ob = new ThreatObserver(){
			 @Override
			public void commitThreat(ThreatSignalInfo info) {
				found = true;
				super.commitThreat(info);
			}
		 };
		 ob.registerDefinition(new ThreatDefManyLoginAttemptsFromSameIPAddress());
		 
		 for(int i = 0; i < 10; i++){
			 ThreatActivityLog log = new ThreatActivityLog().setEndPoint("login").setIpAddress("171.250.110.30").setRequestTime(Long.toString(System.currentTimeMillis()));
			 ob.addLogForDetection(log);
		 }
		
		 assertFalse(found);
		 
		 for(int i = 0; i < 1000000; i++){
			 ThreatActivityLog log = new ThreatActivityLog().setEndPoint("login").setIpAddress("171.250.110.30").setRequestTime(Long.toString(System.currentTimeMillis()));
			 ob.addLogForDetection(log);
		 }
		 
		 
		 waitForAllDetectionsToFinish(ob);
		 
		 
		 
		 assertTrue(found);

	 }
	 
	 private void waitForAllDetectionsToFinish(ThreatObserver ob) {
			while(!ob.isAllDetectionDone()){
	    		try {
					Thread.sleep(1000);
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
