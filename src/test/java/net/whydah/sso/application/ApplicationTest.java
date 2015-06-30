package net.whydah.sso.application;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by totto on 30.06.15.
 */
public class ApplicationTest {

    private static final Logger log = getLogger(ApplicationTest.class);


    @Before
    public void setUp() throws Exception {

    }

    @Ignore
    @Test
    public void testDefaultValuesInApplication() throws Exception {
        Application a = new Application("AppId", "appName");
        //assertTrue("DEFCON5".equalsIgnoreCase(a.getDECFON()));
        assertTrue("0".equalsIgnoreCase(a.getSecurityLevel()));
        assertTrue(Boolean.getBoolean(a.getUserTokenFilter()));
    }

}