package net.whydah.sso.commands.threat;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.util.SystemTestBaseConfig;
import net.whydah.sso.util.WhydahUtil;
import net.whydah.sso.whydah.ThreatSignal;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

import static org.junit.Assert.assertTrue;

public class CommandSendThreatSignalTest {

    private static final Logger log = LoggerFactory.getLogger(CommandSendThreatSignalTest.class);
    public static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testCommandSendThreatSignalTest() throws Exception {

        if (config.isSystemTestEnabled()) {

            String myAppTokenXml;
            myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);

            WhydahApplicationSession.getInstance(config.tokenServiceUri.toString(), config.appCredential);

            String threatResult = new CommandSendThreatSignal(config.tokenServiceUri, myApplicationTokenID, "Systest - test threat signal 1 - Instant: " + WhydahUtil.getRunningSince()).execute();
            assertTrue(threatResult.length() == 0);
        }

    }

    @Test
    public void testCommandSendThreatSignalTest2() throws Exception {

        if (config.isSystemTestEnabled()) {

            String myAppTokenXml;
            myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);

            ThreatSignal threatSignal = new ThreatSignal("Test threatsignal 2");
            threatSignal.setInstant(Instant.now().toString());
            threatSignal.setSignalEmitter("Java-SDK-SYSTEST");
            threatSignal.setSignalSeverity("LOW");
            threatSignal.setSource("CommandSendThreatSignalTest.class");

            String threatResult = new CommandSendThreatSignal(config.tokenServiceUri, myApplicationTokenID, threatSignal).execute();
            assertTrue(threatResult.length() == 0);
        }

    }

    @Test
    public void testCommandSendThreatSignalTest3() throws Exception {

        if (config.isSystemTestEnabled()) {

            String myAppTokenXml;
            myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);

            WhydahApplicationSession.getInstance(config.tokenServiceUri.toString(), config.appCredential);

            ThreatSignal threatSignal = WhydahApplicationSession.createThreat("Threatsignal from Systests 3");

            String threatResult = new CommandSendThreatSignal(config.tokenServiceUri, myApplicationTokenID, threatSignal).execute();
            assertTrue(threatResult.length() == 0);
        }

    }

    @Test
    public void testCommandSendThreatSignalTest4() throws Exception {

        if (config.isSystemTestEnabled()) {

            WhydahApplicationSession applicationSession = WhydahApplicationSession.getInstance(config.tokenServiceUri.toString(), config.appCredential);

            applicationSession.reportThreatSignal("TestSignal from SYStest 4");
            Thread.sleep(1000);  // Give quere() a chance to send the signal before test end
            log.debug("Done");
        }

    }
}
