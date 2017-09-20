package net.whydah.sso.application;

import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;
import net.whydah.sso.application.types.Application;
import net.whydah.sso.whydah.DEFCON;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;


public class ApplicationTest {

    private static final Logger log = getLogger(ApplicationTest.class);


    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testDefaultValuesInApplication() throws Exception {
        Application a = new Application("AppId", "appName");
        assertTrue(DEFCON.DEFCON5 == a.getSecurity().getMinimumDEFCONLevel());
        assertTrue(a.getSecurity().getMinSecurityLevel()==0);
        assertTrue(Boolean.valueOf(a.getSecurity().getUserTokenFilter()));
    }

}