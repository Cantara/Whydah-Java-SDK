package net.whydah.sso.util;

import org.junit.Test;

import static net.whydah.sso.util.LoggerUtil.first50;
import static org.junit.Assert.*;

public class LoggerUtilTest {
    @Test
    public void testFirst50() throws Exception {
        String text = null;
        assertNull(first50(null));
        text = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        assertTrue(50 == first50(text).length());
        text = "bbbb";
        assertEquals(4, first50(text).length());

    }
}