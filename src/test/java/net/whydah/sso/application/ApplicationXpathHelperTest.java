package net.whydah.sso.application;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by totto on 24.06.15.
 */
public class ApplicationXpathHelperTest {

    private static final Logger log = getLogger(ApplicationXpathHelperTest.class);



    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetUserRoleFromUserToken() throws Exception {
        String applications[] = ApplicationXpathHelper.getApplicationNamesFromApplicationsJson(ApplicationHelper.getDummyAppllicationListJson());
        System.out.println("Found applications "+applications.length);
        assertTrue(7 == applications.length);
        assertTrue("SecurityTokenService".equalsIgnoreCase(applications[0]));
        assertTrue("m2Circle".equalsIgnoreCase(applications[6]));
        for(String s : applications)
            System.out.println("ApplicationName: "+s);

    }

    @Test
    public void testFindApplicationNameFromApplicationId() throws Exception {
        String applicationName = ApplicationXpathHelper.findApplicationNameFromApplicationId(ApplicationHelper.getDummyAppllicationListJson());
            System.out.println("ApplicationName: " + applicationName);

    }


}
