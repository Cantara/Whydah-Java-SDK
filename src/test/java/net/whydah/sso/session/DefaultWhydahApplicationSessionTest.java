package net.whydah.sso.session;

import net.whydah.sso.session.experimental.WhydahApplicationSession3;
import net.whydah.sso.simulator.WhydahSimulator;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class DefaultWhydahApplicationSessionTest {

    @Test
    public void thatLogonIsOnlyCalledOnceWithinTokenLifespan() throws InterruptedException {
        try (WhydahSimulator simulator = WhydahSimulator.builder()
                .withMaxNumberOfAllowedLogons(1)
                .build()) {
            WhydahApplicationSession3 was = simulator.createNewSession(builder -> builder.withApplicationSessionCheckIntervalInSeconds(1));
            String activeApplicationTokenId = was.getActiveApplicationTokenId();
            was.hasActiveSession();
            simulator.expectPeriodWithoutAnyErrors(15, TimeUnit.SECONDS);
            System.out.printf("SUCCESS: activeApplicationTokenId: %s%n", activeApplicationTokenId);
        }
    }
}
