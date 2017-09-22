package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.helpers.ApplicationTokenXpathHelper;
import net.whydah.sso.application.mappers.ApplicationCredentialMapper;
import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.session.baseclasses.CryptoUtil;
import net.whydah.sso.session.baseclasses.ExchangeableKey;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandRenewApplicationTokenTest {


    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
        String applicationID = ApplicationTokenXpathHelper.getApplicationIDFromApplicationCredential(ApplicationCredentialMapper.toXML(config.appCredential));
        ExchangeableKey exchangeableKey = new ExchangeableKey("{\"encryptionKey\":\"ZmVlNTZiYjU4MWMzOTc3YzM0YWMzNTZiOWJlYjhhY2I=\",\n" +
                "\"iv\":\"MDEyMzQ1Njc4OTBBQkNERQ==\"}");
        CryptoUtil.setExchangeableKey(exchangeableKey);
    }


    @Test
    @Ignore
    public void testCommandRenewApplicationTokenTest() throws Exception {

        if (config.isSystemTestEnabled()) {

            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
            // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
            assertTrue(myAppTokenXml != null);
            assertTrue(myAppTokenXml.length() > 6);
            ApplicationToken at = ApplicationTokenMapper.fromXml(myAppTokenXml);

            assertTrue(new CommandValidateApplicationTokenId(config.tokenServiceUri.toString(), at.getApplicationTokenId()).execute());

            String applicationTokenXml = new CommandRenewApplicationSession(config.tokenServiceUri, at.getApplicationTokenId()).execute();
            assertTrue(applicationTokenXml != null);
            assertTrue(applicationTokenXml.length() > 6);
            ApplicationToken at2 = ApplicationTokenMapper.fromXml(applicationTokenXml);
            assertTrue(at2.getApplicationID().equalsIgnoreCase(config.appCredential.getApplicationID()));

        }
    }

}
