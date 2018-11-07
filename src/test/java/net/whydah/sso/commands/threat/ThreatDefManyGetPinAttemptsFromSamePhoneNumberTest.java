package net.whydah.sso.commands.threat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;

import org.junit.Test;
import org.slf4j.Logger;

public class ThreatDefManyGetPinAttemptsFromSamePhoneNumberTest {
	 private static final Logger logger = getLogger(ThreatDefManyGetPinAttemptsFromSamePhoneNumberTest.class);
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
		 ob.registerDefinition(new ThreatDefManyGetPinAttemptsFromSamePhoneNumber(5));
		 
		 for(int i = 0; i < 4; i++){
			 ThreatActivityLog log = new ThreatActivityLog("/getPin", "171.250.110.30", Long.toString(System.currentTimeMillis()), new Object[] {"phoneNo", "59441023"});
			 ob.addLogForDetection(log);
		 }
		
		 assertFalse(found);
		 
		 //try one more
		 ThreatActivityLog log = new ThreatActivityLog("/getPin", "171.250.110.30", Long.toString(System.currentTimeMillis()), new Object[] {"phoneNo", "59441023"});
		 ob.addLogForDetection(log);
		 
		 
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
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
