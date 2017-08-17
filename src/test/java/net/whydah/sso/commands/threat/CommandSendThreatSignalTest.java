package net.whydah.sso.commands.threat;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.util.SystemTestBaseConfig;
import net.whydah.sso.util.WhydahUtil;
import net.whydah.sso.whydah.ThreatSignal;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

import static junit.framework.TestCase.assertTrue;

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
            Assert.assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);

            String threatResult = new CommandSendThreatSignal(config.tokenServiceUri, myApplicationTokenID, "Systest - test threat signal - Instant: " + WhydahUtil.getRunningSince()).execute();
            assertTrue(threatResult.length() == 0);
        }

    }

    @Test
    public void testCommandSendThreatSignalTest2() throws Exception {

        if (config.isSystemTestEnabled()) {

            String myAppTokenXml;
            myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            Assert.assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);

            ThreatSignal threatSignal = new ThreatSignal("Test threatsignal");
            threatSignal.setInstant(Instant.now().toString());
            threatSignal.setSignalEmitter("Java-SDK-SYSTEST");
            threatSignal.setSignalSeverity("LOW");
            threatSignal.setSource("CommandSendThreatSignalTest.class");

            String threatResult = new CommandSendThreatSignal(config.tokenServiceUri, myApplicationTokenID, threatSignal).execute();
            assertTrue(threatResult.length() == 0);
        }

    }
}
