package net.whydah.sso.util;

/**
 * Mask information in an logstatement
 * Created by baardl on 2017-03-31.
 */
public class LoggerUtil {

    public static String first(Object object, int numChars) {
        String first = null;
        if (object != null) {
            String text = object.toString();
            if (text != null) {
                if (text.length() > numChars) {
                    first = text.substring(0, numChars).replace("\n", " ");
                } else {
                    first = text;
                }
            }
        }

        return first;

    }

    public static String first50(Object object) {
        return first(object, 50);
    }
}
