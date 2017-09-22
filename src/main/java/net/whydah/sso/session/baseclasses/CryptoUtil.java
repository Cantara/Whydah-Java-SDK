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

import static org.slf4j.LoggerFactory.getLogger;

public class CryptoUtil {

    private static final Logger log = getLogger(CryptoUtil.class);


    private static ExchangeableKey myOldKey = new ExchangeableKey();
    private static ExchangeableKey myKey = new ExchangeableKey();

    public static void setExchangeableKey(ExchangeableKey exchangeableKey) throws Exception {
        myOldKey.setEncryptionKey(myKey.getEncryptionKey());
        myOldKey.setIv(myKey.getIv());
        myKey = exchangeableKey;
    }

    public static void setEncryptionSecretAndIv(String secret, IvParameterSpec ivp) throws Exception {
        myOldKey.setEncryptionKey(myKey.getEncryptionKey());
        myOldKey.setIv(myKey.getIv());
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, 65536, 256); // AES-256
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        myKey.setEncryptionKey(f.generateSecret(spec).getEncoded());
        myKey.setIv(ivp);
        log.debug(myKey.toString());
    }




    public static String encrypt(String sampleText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec((myKey.getEncryptionKey()), "AES"), myKey.getIv());
        String encrypted = Hex.encodeHexString(cipher.doFinal((sampleText.toString()).getBytes()));
        return encrypted;
    }

    public static String decrypt(String enc) throws Exception {
        if (checkForBase64EncodesdString(enc)) {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec((myKey.getEncryptionKey()), "AES"), myKey.getIv());
                String decrypted = new String(cipher.doFinal(Hex.decodeHex(enc.toCharArray())));
                return decrypted;
            } catch (Exception e) {
                log.warn("Exception in trying to decrypt message, trying fallback to old key", e.getMessage());
                if (myOldKey != null) {
                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec((myOldKey.getEncryptionKey()), "AES"), myOldKey.getIv());
                    String decrypted = new String(cipher.doFinal(Hex.decodeHex(enc.toCharArray())));
                    return decrypted;

                }
            }

        }
        return enc;  // not encoded string, so we return the raw string
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
