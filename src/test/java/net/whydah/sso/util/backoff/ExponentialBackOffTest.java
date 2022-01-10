package net.whydah.sso.util.backoff;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExponentialBackOffTest {

    @Test
    public void thatStableExpBackoffWorks() {
        ExponentialBackOff exponentialBackOff = new ExponentialBackOff(1000, 1.5);
        exponentialBackOff.setMaxInterval(60000);
        BackOffExecution backOffExecution = exponentialBackOff.start();
        long total = 0;
        long next;
        int N = 20;
        for (int i = 0; i< N && ((next = backOffExecution.nextBackOff()) != BackOffExecution.STOP); i++) {
            total += next;
        }
        assertEquals(710966, total);
    }

    @Test
    public void thatJitteryExpBackoffWorks() {
        int jitterRange = 2000;
        ExponentialBackOff exponentialBackOff = new JitteryExponentialBackOff(1000, 1.5, jitterRange);
        exponentialBackOff.setMaxInterval(60000);
        BackOffExecution backOffExecution = exponentialBackOff.start();
        long total = 0;
        long next;
        int N = 20;
        for (int i = 0; i< N && ((next = backOffExecution.nextBackOff()) != BackOffExecution.STOP); i++) {
            total += next;
        }
        Assert.assertTrue(total > (710966 - N * jitterRange));
        Assert.assertTrue(total < (710966 + N * jitterRange));
    }
}