package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static org.junit.Assert.assertTrue;

public class CommandGetApplicationNameFromApplicationTokenIdTest {
    private static URI tokenServiceUri;
    private static ApplicationCredential appCredential;
    private static boolean systemTest = false;

    @BeforeClass
    public static void setup() throws Exception {
        appCredential = new ApplicationCredential("2215", "Whydah-SSOLoginWebApp", "FF779936R6Jr47D4Hj5R6p9qT");
        tokenServiceUri = UriBuilder.fromUri("https://no_host").build();


        if (systemTest) {
            tokenServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/tokenservice/").build();
        }
    }


    @Test
    public void testCommandGetApplicationNameFromApplicationTokenId() throws Exception {
        if (systemTest) {
            tokenServiceUri = UriBuilder.fromUri("https://whydahdev.cantara.no/tokenservice/").build();

            String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID != null && myApplicationTokenID.length() > 5);
            String applicationID = new CommandGetApplicationIdFromApplicationTokenId(tokenServiceUri, myApplicationTokenID).execute();

            System.out.println("Found applicationID: {}" + applicationID);

        }
    }
}
