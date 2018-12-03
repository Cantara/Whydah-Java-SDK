package net.whydah.sso.commands.threat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;

import org.junit.Test;
import org.slf4j.Logger;

public class ThreatDefTooManyRequestsForOneEndpointTest {

	private static final Logger logger = getLogger(ThreatDefTooManyRequestsForOneEndpointTest.class);
	boolean found = false;

	@Test
	public void testOperation() throws InterruptedException
	{
		ThreatDefTooManyRequestsForOneEndpoint.COUNT = 100;
		
		ThreatObserver ob = new ThreatObserver(){
			@Override
			public void commitThreat(ThreatSignalInfo info) {
				found = true;
				super.commitThreat(info);
			}
		};
		
		ob.registerDefinition(new ThreatDefTooManyRequestsForOneEndpoint());

		for(int i = 0; i < 10; i++){
			ThreatActivityLog log = new ThreatActivityLog().setEndPoint("/user-data").setIpAddress("171.250.110.30").setRequestTime(Long.toString(System.currentTimeMillis()));
			ob.addLogForDetection(log);
		}

		waitForDetectionProcessTriggered(ob);
		
		assertFalse(found);

		for(int i = 0; i < ThreatDefTooManyRequestsForOneEndpoint.COUNT; i++){
			ThreatActivityLog log = new ThreatActivityLog().setEndPoint("/user-data").setIpAddress("171.250.110.*").setRequestTime(Long.toString(System.currentTimeMillis()));
			ob.addLogForDetection(log);
		}


		waitForDetectionProcessTriggered(ob);



		assertTrue(found);

	}
	 private void waitForDetectionProcessTriggered(ThreatObserver ob) {
			while(!ob.isDetectionDone()){
	    		try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					
				} 
	    	}
	}

}
