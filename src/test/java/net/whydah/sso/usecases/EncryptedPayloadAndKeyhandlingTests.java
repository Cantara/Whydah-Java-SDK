package net.whydah.sso.usecases;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class EncryptedPayloadAndKeyhandlingTests {



    @Test
    public void testCreateAndShipKeys() throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

//        KeySpec spec = new PBEKeySpec("password".toCharArray(), salt, 65536, 256); // AES-256
//        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//        byte[] key = f.generateSecret(spec).getEncoded();

        byte[] ivBytes = new byte[16];
        random.nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);




        String testData = "Hello World";
        String sharedKey = Hex.encodeHexString(UUID.randomUUID().toString().getBytes()).substring(0, 32);

        String encryptedText = encrypt(testData, sharedKey, iv);
        assertNotNull(encryptedText);
        String result = decrypt(encryptedText, sharedKey, iv);
        assertTrue(result.equalsIgnoreCase(testData));

    }


    private String encrypt(String sampleText, String encryptionKey, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Hex.decodeHex(encryptionKey.toCharArray()), "AES"), iv);
        String encrypted = Hex.encodeHexString(cipher.doFinal((sampleText.toString()).getBytes()));
        return encrypted;
    }

    private String decrypt(String enc, String encryptionKey, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Hex.decodeHex(encryptionKey.toCharArray()), "AES"), iv);
        String decrypted = new String(cipher.doFinal(Hex.decodeHex(enc.toCharArray())));
        return decrypted;
    }

    @Test
    public void testRSAPublicKeySetup() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        Key pub = kp.getPublic();
        Key pvt = kp.getPrivate();

        Base64.Encoder encoder = Base64.getEncoder();
        String base64publicKey = encoder.encodeToString(kp.getPublic().getEncoded());

    }
}
