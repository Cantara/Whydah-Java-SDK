package net.whydah.sso.session;

import net.whydah.sso.whydah.DEFCON;

public class DEFCONHandler {
    private static DEFCON myAppDEFCONLevel = DEFCON.DEFCON1;

    public static void setMyAppDEFCONLevel(DEFCON myAppDEFCONLevel) {
        DEFCONHandler.myAppDEFCONLevel = myAppDEFCONLevel;
    }

    public static DEFCON getMyAppDEFCONLevel() {
        return myAppDEFCONLevel;
    }

    public static void handleDefcon(DEFCON defcon) {
        if (DEFCON.DEFCON5 == defcon) {
            // Do nothing, this is fine
        } else if (DEFCON.DEFCON4 == defcon) {
            // If application is a DEFCON5 applikation - system exit
            if (myAppDEFCONLevel == DEFCON.DEFCON5) {
                System.exit(1);
            }
        } else if (DEFCON.DEFCON3 == defcon) {
            // If application is a DEFCON5 or DEFCON4 application - system exit
            if (myAppDEFCONLevel == DEFCON.DEFCON5 || myAppDEFCONLevel == DEFCON.DEFCON4) {
                System.exit(1);
            }
        } else if (DEFCON.DEFCON2 == defcon) {
            // If application is a DEFCON5 or DEFCON4 or DEFCON3 application - system exit
            if (myAppDEFCONLevel == DEFCON.DEFCON5 || myAppDEFCONLevel == DEFCON.DEFCON4 || myAppDEFCONLevel == DEFCON.DEFCON3) {
                System.exit(1);
            }
        } else if (DEFCON.DEFCON1 == defcon) {
            // We are in reaaly bad shit here...


        }
    }
}
