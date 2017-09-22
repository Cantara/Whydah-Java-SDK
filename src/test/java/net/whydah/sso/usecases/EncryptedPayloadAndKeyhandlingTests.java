package net.whydah.sso.usecases;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class EncryptedPayloadAndKeyhandlingTests {


    @Test
    public void testCreateAndShipKeys() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        Key pub = kp.getPublic();
        Key pvt = kp.getPrivate();

        Base64.Encoder encoder = Base64.getEncoder();
        String base64publicKey = encoder.encodeToString(kp.getPublic().getEncoded());

        String testData = "Hello World";
        String sharedKey = Hex.encodeHexString(UUID.randomUUID().toString().getBytes()).substring(0, 32);

        String encryptedText = encrypt(testData, sharedKey);
        assertNotNull(encryptedText);
        String result = decrypt(encryptedText, sharedKey);
        assertTrue(result.equalsIgnoreCase(testData));

    }


    private String encrypt(String sampleText, String encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Hex.decodeHex(encryptionKey.toCharArray()), "AES"));
        String encrypted = Hex.encodeHexString(cipher.doFinal((sampleText.toString()).getBytes()));
        return encrypted;
    }

    private String decrypt(String enc, String encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Hex.decodeHex(encryptionKey.toCharArray()), "AES"));
        String decrypted = new String(cipher.doFinal(Hex.decodeHex(enc.toCharArray())));
        return decrypted;
    }
}
