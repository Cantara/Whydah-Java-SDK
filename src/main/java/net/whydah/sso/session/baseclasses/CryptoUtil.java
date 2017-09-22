package net.whydah.sso.session.baseclasses;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CryptoUtil {

    public static String encrypt(String sampleText, String encryptionKey, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Hex.decodeHex(encryptionKey.toCharArray()), "AES"), iv);
        String encrypted = Hex.encodeHexString(cipher.doFinal((sampleText.toString()).getBytes()));
        return encrypted;
    }

    public static String decrypt(String enc, String encryptionKey, IvParameterSpec iv) throws Exception {
        if (checkForBase64EncodesdString(enc)) {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Hex.decodeHex(encryptionKey.toCharArray()), "AES"), iv);
            String decrypted = new String(cipher.doFinal(Hex.decodeHex(enc.toCharArray())));
            return decrypted;

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
