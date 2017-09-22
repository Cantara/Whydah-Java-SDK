package net.whydah.sso.session.baseclasses;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {

    public static String encrypt(String sampleText, String encryptionKey, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Hex.decodeHex(encryptionKey.toCharArray()), "AES"), iv);
        String encrypted = Hex.encodeHexString(cipher.doFinal((sampleText.toString()).getBytes()));
        return encrypted;
    }

    public static String decrypt(String enc, String encryptionKey, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Hex.decodeHex(encryptionKey.toCharArray()), "AES"), iv);
        String decrypted = new String(cipher.doFinal(Hex.decodeHex(enc.toCharArray())));
        return decrypted;
    }
}
