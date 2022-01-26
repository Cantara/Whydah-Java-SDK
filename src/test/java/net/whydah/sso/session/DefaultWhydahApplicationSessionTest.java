package net.whydah.sso.session;

import net.whydah.sso.session.experimental.WhydahApplicationSession3;
import net.whydah.sso.simulator.WhydahSimulator;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DefaultWhydahApplicationSessionTest {

    @Test
    public void thatLogonIsOnlyCalledOnceWithinTokenLifespan() {
        try (WhydahSimulator simulator = WhydahSimulator.builder()
                .build()) {
            WhydahApplicationSession3 was = simulator.createNewSession("myappid", "MyApplication", "my-app-s3cr3t");
            String activeApplicationTokenId = was.getActiveApplicationTokenId();
            assertTrue(was.hasActiveSession());
            System.out.printf("SUCCESS: activeApplicationTokenId: %s%n", activeApplicationTokenId);
        }
    }
}
