package net.whydah.sso.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class IsAdminSDKTest {

    @Test
    public void testIsAdminSDKUtil() {
        assertFalse(WhydahUtil.isAdminSdk());
    }

    @Test
    public void testIsAdminSDKUtil2() {
        assertFalse(WhydahUtil2.isAdminSdk());
    }
}
