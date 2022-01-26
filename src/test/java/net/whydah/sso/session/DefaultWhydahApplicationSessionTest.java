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
            simulator.expectPeriodWithoutAnyErrors(3, TimeUnit.SECONDS);
            assertNotNull(was.getActiveApplicationTokenId());
        }
    }
}
