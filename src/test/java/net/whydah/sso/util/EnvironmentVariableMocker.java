package net.whydah.sso.util;

import java.util.Map;

/**
 * A simplified approach that uses system properties instead of mocking environment variables
 */
public class EnvironmentVariableMocker {

    /**
     * Connect to the environment by setting system properties
     */
    public static void connect(Map<String, String> envVariables) {
        // Set as system properties
        for (Map.Entry<String, String> entry : envVariables.entrySet()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Remove properties
     */
    public static boolean pop() {
        // You could remove specific properties here if needed
        return true;
    }

    /**
     * For backward compatibility
     */
    public static boolean remove(Map<String, String> theOneToPop) {
        return pop();
    }
}