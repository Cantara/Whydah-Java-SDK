package net.whydah.sso.util;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by totto on 30.06.15.
 */
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
