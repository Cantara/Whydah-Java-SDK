package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.session.baseclasses.CryptoUtil;
import net.whydah.sso.session.baseclasses.ExchangeableKey;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;

import static net.whydah.sso.util.LoggerUtil.first50;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

public class CommandRenewApplicationTokenTest {

    private static final Logger log = getLogger(CommandRenewApplicationTokenTest.class);



    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }


    @Test
    //@Ignore
    public void testCommandRenewWithCryptoApplicationTokenTest() throws Exception {

        if (config.isSystemTestEnabled()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
            assertTrue(myAppTokenXml != null);
            assertTrue(myAppTokenXml.length() > 6);
            ApplicationToken applicationToken = ApplicationTokenMapper.fromXml(myAppTokenXml);

            assertTrue(new CommandValidateApplicationTokenId(config.tokenServiceUri.toString(), applicationToken.getApplicationTokenId()).execute());


            // Lets loop a few times
            String exchangeableKeyString;
            ExchangeableKey exchangeableKey;
            String applicationTokenXml;
            ApplicationToken applicationToken1;

            for (int n = 0; n < 10; n++) {
                exchangeableKeyString = new CommandGetApplicationKey(config.tokenServiceUri, applicationToken.getApplicationTokenId()).execute();
                log.debug("{} Found exchangeableKeyString: {}", n, exchangeableKeyString);
                exchangeableKey = new ExchangeableKey(exchangeableKeyString);
                log.debug("{} Found exchangeableKey: {}", n, exchangeableKey);
                CryptoUtil.setExchangeableKey(exchangeableKey);
                applicationTokenXml = new CommandRenewApplicationSession(config.tokenServiceUri, applicationToken.getApplicationTokenId()).execute();
                assertTrue(applicationTokenXml != null);
                assertTrue(applicationTokenXml.length() > 6);
                applicationToken1 = ApplicationTokenMapper.fromXml(applicationTokenXml);
                log.debug("{n} Updated ApplicationToken: {}", first50(applicationToken1));
                assertTrue(applicationToken1.getApplicationID().equalsIgnoreCase(config.appCredential.getApplicationID()));
                Thread.sleep(5000);

            }

        }
    }

}
