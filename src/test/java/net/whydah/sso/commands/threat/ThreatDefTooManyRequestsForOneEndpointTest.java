package net.whydah.sso.commands.threat;

import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

public class ThreatDefTooManyRequestsForOneEndpointTest {

	private static final Logger logger = getLogger(ThreatDefTooManyRequestsForOneEndpointTest.class);
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

		ob.registerDefinition(new ThreatDefTooManyRequestsForOneEndpoint(100));

		for(int i = 0; i < 10; i++){
			ThreatActivityLog log = new ThreatActivityLog().setEndPoint("/user-data").setIpAddress("171.250.110.30").setRequestTime(Long.toString(System.currentTimeMillis()));
			ob.addLogForDetection(log);
		}

		waitForDetectionProcessTriggered(ob);

		assertFalse(found);

		for(int i = 0; i < 100; i++){
			ThreatActivityLog log = new ThreatActivityLog().setEndPoint("/user-data").setIpAddress("171.250.110.*").setRequestTime(Long.toString(System.currentTimeMillis()));
			ob.addLogForDetection(log);
		}


		waitForDetectionProcessTriggered(ob);



		assertTrue(found);

	}
	private void waitForDetectionProcessTriggered(ThreatObserver ob) throws InterruptedException {
		//have to wait for the next schedule
		Thread.sleep(ThreatObserver.LOGS_CHECK_INTERVAL*1000+2000);
		//just wait for the result if detection process is running
		while(!ob.isDetectionDone()){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {

			} 
		}
	}

}
