package net.whydah.sso.session.baseclasses;

import net.whydah.sso.basehelpers.JsonPathHelper;
import org.slf4j.Logger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import static net.whydah.sso.util.LoggerUtil.first50;
import static org.slf4j.LoggerFactory.getLogger;

public class ExchangeableKey {
    private static final Logger log = getLogger(ExchangeableKey.class);


    private byte[] encryptionKey;
    private IvParameterSpec iv;
    Base64.Encoder encoder = Base64.getEncoder();

    public ExchangeableKey(byte[] encryptionKey, IvParameterSpec iv) {
        this.encryptionKey = encryptionKey;
        this.iv = iv;
    }

    public ExchangeableKey(String jsonEncodedKey) {
        log.debug("Constructor called with {}", first50(jsonEncodedKey));
        try {
            String encryptionKeyEncoded = JsonPathHelper.findJsonPathValue(jsonEncodedKey, "$.encryptionKey");
            String iv = JsonPathHelper.findJsonPathValue(jsonEncodedKey, "$.iv");
            Base64.Decoder decoder = Base64.getDecoder();

            setEncryptionKey(decoder.decode(encryptionKeyEncoded));
            setIv(new IvParameterSpec(decoder.decode(iv)));
        } catch (Exception e) {
            log.warn("Error trying to initialize key form string.  Input:", jsonEncodedKey);
        }
    }

    public ExchangeableKey()  {
    	
        
    }

    public byte[] getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(byte[] encryptionKey) {
        if (encryptionKey != null) {
            this.encryptionKey = encryptionKey.clone();
        }
    }

    public IvParameterSpec getIv() {
        return iv;
    }

    public void setIv(IvParameterSpec iv) {
        this.iv = iv;
    }

    public String toJsonEncoded() {
        return "{" +
                "\"encryptionKey\":\"" + encoder.encodeToString(encryptionKey) + "\",\n" +
                "\"iv\":\"" + encoder.encodeToString(iv.getIV()) + "\"" +
                '}';
    }

    /**
     * Alterative method to generate cryptokey from secret
     *
     * @param secret the string we convert into a special key
     * @throws Exception
     */
    public void setEncryptionSecret(String secret) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, 65536, 256); // AES-256
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        setEncryptionKey(f.generateSecret(spec).getEncoded());

    }
}
