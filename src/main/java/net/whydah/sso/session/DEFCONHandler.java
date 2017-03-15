package net.whydah.sso.session;

import net.whydah.sso.whydah.DEFCON;

public class DEFCONHandler {

    public static void handleDefcon(DEFCON defcon) {
        if (DEFCON.DEFCON5 == defcon) {
            // Do nothing, this is fine
        } else if (DEFCON.DEFCON4 == defcon) {
            // If application is a DEFCON5 applikation - system exit
        } else if (DEFCON.DEFCON3 == defcon) {
            // If application is a DEFCON5 or DEFCON4 application - system exit

        } else if (DEFCON.DEFCON2 == defcon) {

        } else if (DEFCON.DEFCON1 == defcon) {

        }
    }
}
