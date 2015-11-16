package net.whydah.sso.application;

import net.whydah.sso.application.helpers.ApplicationHelper;
import net.whydah.sso.application.helpers.ApplicationJsonpathHelper;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by totto on 24.06.15.
 */
public class ApplicationXpathHelperTest {

    private static final Logger log = getLogger(ApplicationXpathHelperTest.class);
    private static String applicationTokenXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n" +
            "  <applicationtoken>\n" +
            "     <params>\n" +
            "         <applicationtokenID>757b505cfd34c64c85ca5b5690ee5293</applicationtokenID>\n" +
            "         <applicationid>201</applicationid>\n" +
            "         <applicationname></applicationname>\n" +
            "         <expires>1435242569010</expires>\n" +
            "     </params> \n" +
            "     <Url type=\"application/xml\" method=\"POST\"                 template=\"http://localhost:9998/tokenservice/user/757b505cfd34c64c85ca5b5690ee5293/get_usertoken_by_usertokenid\"/> \n" +
            " </applicationtoken>";

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testFindExpiresFromApplicationToken() throws Exception  {
        Long expires = 1435242569010L;
        assertEquals(expires, ApplicationXpathHelper.getExpiresFromAppTokenXml(applicationTokenXml));
    }

    @Test
    public void testFindAppTokenId() throws Exception  {
        String appTokenId = "757b505cfd34c64c85ca5b5690ee5293";
        assertEquals(appTokenId,ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(applicationTokenXml));
    }

    @Test
    public void testGetUserRoleFromUserToken() throws Exception {
        String applications[] = ApplicationJsonpathHelper.getApplicationNamesFromApplicationsJson(ApplicationHelper.getDummyAppllicationListJson());
        System.out.println("Found applications "+applications.length);
        assertTrue(7 < applications.length);
        assertTrue("ACS".equalsIgnoreCase(applications[0]));
        assertTrue("m2Circle".equalsIgnoreCase(applications[6]));
        for(String s : applications)
            System.out.println("ApplicationName: "+s);

    }

    @Ignore   // TODO  Make this jsonpath work..
    @Test
    public void testFindApplicationNameFromApplicationId() throws Exception {
        String applicationName = ApplicationJsonpathHelper.findApplicationNameFromApplicationId(ApplicationHelper.getDummyAppllicationListJson());
            System.out.println("ApplicationName: " + applicationName);

    }


}
