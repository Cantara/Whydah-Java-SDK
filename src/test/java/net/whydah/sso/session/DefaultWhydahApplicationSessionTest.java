package net.whydah.sso.session;

import net.whydah.sso.session.experimental.WhydahApplicationSession3;
import net.whydah.sso.simulator.WhydahSimulator;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DefaultWhydahApplicationSessionTest {

    @Test
    public void thatLogonIsOnlyCalledOnceWithinTokenLifespan() throws InterruptedException {
        try (WhydahSimulator simulator = WhydahSimulator.builder()
                .withMaxNumberOfAllowedLogons(1)
                .build()) {
            WhydahApplicationSession3 was = simulator.createNewSession(builder -> builder.withApplicationSessionCheckIntervalInSeconds(1));
            assertTrue(was.hasActiveSession());
            simulator.expectPeriodWithoutAnyErrors(3, TimeUnit.SECONDS); // logon works and a bug causing another call to logon would be expected after 1 second
            assertNotNull(was.getActiveApplicationTokenId());
        }
    }

    @Test
    public void thatFailingLogonUsesBackoff() throws InterruptedException {
        try (WhydahSimulator simulator = WhydahSimulator.builder()
                .withApplicationLogonAlwaysFailing(true)
                .withMaxNumberOfAllowedLogons(5) // We do not expect to get more than 4-5 logons with configured exponential backoff parameters
                .build()) {
            WhydahApplicationSession3 was = simulator.createNewSession(builder -> builder.withApplicationSessionCheckIntervalInSeconds(1));
            simulator.expectPeriodWithoutAnyErrors(15, TimeUnit.SECONDS); // if backoff is not used (due to unexpected bug) it would be around 1 logon per second causing this check to fail
        }
    }
}
