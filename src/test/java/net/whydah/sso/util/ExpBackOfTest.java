package net.whydah.sso.util;


import com.netflix.hystrix.exception.HystrixRuntimeException;
import net.whydah.sso.commands.threat.CommandSendThreatSignal;
import net.whydah.sso.util.backoff.BackOff;
import net.whydah.sso.util.backoff.BackOffExecution;
import net.whydah.sso.util.backoff.ExponentialBackOff;
import org.junit.Test;
import org.slf4j.Logger;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

public class ExpBackOfTest {

    private static final Logger log = getLogger(ExpBackOfTest.class);

    @Test
    public void testAndDocumentExpotensialBackOff() {
        BackOff exponentialBackOff = new ExponentialBackOff();
        BackOffExecution backOffExecution = exponentialBackOff.start();

        URI dummyTokerServiceURL = UriBuilder.fromUri("https://no_host").build();

        int i = 0;
        while (i < 5) {
            try {
                new CommandSendThreatSignal(dummyTokerServiceURL, "myApppId", "myThreatmessage").execute();
                i = i + 3;
                log.debug("Execution success, i=" + 1);
            } catch (HystrixRuntimeException e) {
                i++;
                log.debug("Execution failed, i=" + 1);
            }
        }
    }

}

