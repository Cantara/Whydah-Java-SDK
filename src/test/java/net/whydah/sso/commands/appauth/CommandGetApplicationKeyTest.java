package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.session.baseclasses.ExchangeableKey;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

public class CommandGetApplicationKeyTest {
    private static final Logger log = getLogger(CommandGetApplicationKeyTest.class);


    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    public void testCommandGetApplicationKey() throws Exception {
        if (config.isSystemTestEnabled()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
            String exchangeableKeyString = new CommandGetApplicationKey(config.tokenServiceUri, myApplicationTokenID).execute();

            log.debug("Found exchangeableKeyString: {}", exchangeableKeyString);
            ExchangeableKey exchangeableKey = new ExchangeableKey(exchangeableKeyString);
            log.debug("Found exchangeableKey: {}", exchangeableKey);


        }
    }
}
