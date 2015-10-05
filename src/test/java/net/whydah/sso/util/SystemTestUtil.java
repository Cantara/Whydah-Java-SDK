package net.whydah.sso.util;

import java.io.IOException;
import java.net.Socket;

public class SystemTestUtil {

    public  static boolean noLocalWhydahRunning() {
        int port=9998;
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }
}

