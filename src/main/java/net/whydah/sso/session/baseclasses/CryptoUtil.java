package net.whydah.sso.session.baseclasses;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.whydah.sso.util.LoggerUtil.first50;
import static org.slf4j.LoggerFactory.getLogger;

public class CryptoUtil {

    private static final Logger log = getLogger(CryptoUtil.class);


    private static ExchangeableKey myOldKey = new ExchangeableKey();
    private static ExchangeableKey myKey = new ExchangeableKey();

    public static void setExchangeableKey(ExchangeableKey exchangeableKey) throws Exception {
        ExchangeableKey existingKey = null;
        if (myKey.getEncryptionKey() != null) {
            existingKey = new ExchangeableKey(myKey.toJsonEncoded());
        }

        if (existingKey != null && myKey.toJsonEncoded().equalsIgnoreCase(existingKey.toJsonEncoded())) {
            log.trace("Do not update, same key");
        } else {
            myOldKey.setEncryptionKey(myKey.getEncryptionKey());
            myOldKey.setIv(myKey.getIv());
        }
        myKey = new ExchangeableKey(exchangeableKey.toJsonEncoded());
        log.trace("Updated key:", first50(myKey.toJsonEncoded()));
    }

    public static void setEncryptionSecretAndIv(String secret, IvParameterSpec ivp) throws Exception {
        ExchangeableKey existingKey = null;
        if (myKey.getEncryptionKey() != null) {
            existingKey = new ExchangeableKey(myKey.toJsonEncoded());
        }
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, 65536, 256); // AES-256
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        myKey.setEncryptionKey(f.generateSecret(spec).getEncoded());
        myKey.setIv(ivp);
        log.trace("Created new key:{}", first50(myKey.toJsonEncoded()));
        if (existingKey != null && myKey.toJsonEncoded().equalsIgnoreCase(existingKey.toJsonEncoded())) {
            log.trace("Do not update, same key");
        } else {
            myOldKey = existingKey;
        }
    }


    public static String encrypt(String sampleText) throws Exception {
        if (isEncryptionEnabled()) {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec((myKey.getEncryptionKey()), "AES"), myKey.getIv());
            return Hex.encodeHexString(cipher.doFinal((sampleText.toString()).getBytes()));
        }
        log.debug("crypto not enabled");
        return sampleText;
    }

    public static String decrypt(String enc) throws Exception {
        log.trace("Decrypting: [}, myKey:{}, myIV:{}", enc, myKey.getEncryptionKey(), myKey.getIv());
        if (isEncryptionEnabled()) {
            if (checkForBase64EncodesdString(enc)) {
                try {
                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec((myKey.getEncryptionKey()), "AES"), myKey.getIv());
                    return new String(cipher.doFinal(Hex.decodeHex(enc.toCharArray())));
                } catch (Exception e) {
                    log.warn("Exception in trying to decrypt message, trying fallback to old key", e);
                }
                try {
                    if (myOldKey.getIv() != null) {
                        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec((myOldKey.getEncryptionKey()), "AES"), myOldKey.getIv());
                        return new String(cipher.doFinal(Hex.decodeHex(enc.toCharArray())));
                    }
                } catch (Exception e) {
                    log.warn("Exception in trying to decrypt message.", e);
                }
            }
        }
        return enc;  // not encoded string, so we return the raw string
    }

    private static boolean isEncryptionEnabled() {
        return myKey.getEncryptionKey() != null;
    }

    public static boolean checkForBase64EncodesdString(String string) {
        String pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(string);
        if (m.find()) {
            return true;
        } else {
            return false;
        }
    }

    public static String getActiveKey() {
        return myKey.toJsonEncoded();
    }
}
