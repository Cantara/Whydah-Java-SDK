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

    private static byte[] oldEncryptionKey;
    private static IvParameterSpec oldIv;
    private static byte[] encryptionKey;
    private static IvParameterSpec iv;


    public static void setEncryptionSrecret(String secret) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, 65536, 256); // AES-256
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        setEncryptionKey(f.generateSecret(spec).getEncoded());
    }

    private static void setEncryptionKey(byte[] encKey) {
        oldEncryptionKey = encryptionKey;
        encryptionKey = encKey;
    }

    public static void setIv(IvParameterSpec ivp) {
        oldIv = iv;
        iv = ivp;
    }


    public static String encrypt(String sampleText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec((encryptionKey), "AES"), iv);
        String encrypted = Hex.encodeHexString(cipher.doFinal((sampleText.toString()).getBytes()));
        return encrypted;
    }

    public static String decrypt(String enc) throws Exception {
        if (checkForBase64EncodesdString(enc)) {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec((encryptionKey), "AES"), iv);
                String decrypted = new String(cipher.doFinal(Hex.decodeHex(enc.toCharArray())));
                return decrypted;
            } catch (Exception e) {
                log.warn("Exception in trying to decrypt message, trying fallback to old key", e);
                if (oldEncryptionKey != null) {
                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec((oldEncryptionKey), "AES"), oldIv);
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
}
