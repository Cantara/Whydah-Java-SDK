package net.whydah.sso.session.baseclasses;

import org.constretto.ConstrettoConfiguration;
import org.constretto.exception.ConstrettoConversionException;
import org.constretto.exception.ConstrettoExpressionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WhydahInternalConstrettoUtils {

    private static final Logger log = LoggerFactory.getLogger(WhydahInternalConstrettoUtils.class);

    static String getStringOrDefault(ConstrettoConfiguration configuration, String key, String defaultValue) {
        if (configuration.hasValue(key)) {
            try {
                return configuration.evaluateToString(key);
            } catch (ConstrettoExpressionException | ConstrettoConversionException constrettoExpressionException) {
                log.warn(key + " constretto configuration was not found, using default value of: " + defaultValue);
                return defaultValue;
            }
        }
        return defaultValue;
    }

    static String getStringOrThrow(ConstrettoConfiguration configuration, String key) {
        if (configuration.hasValue(key)) {
            return configuration.evaluateToString(key);
        } else {
            throw new IllegalArgumentException(key + " not found in constretto configuration");
        }
    }
}
