package net.whydah.sso.usecases;

import net.whydah.sso.session.baseclasses.CryptoUtil;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.apache.commons.codec.binary.Hex;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static net.whydah.sso.session.baseclasses.CryptoUtil.decrypt;
import static net.whydah.sso.session.baseclasses.CryptoUtil.encrypt;

public class EncryptedPayloadAndKeyhandlingTests {

    static SystemTestBaseConfig config;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }



    @Test
    public void testCreateAndShipKeys() throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        byte[] ivBytes = new byte[16];
        random.nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);




        String testData = "Hello World";

        CryptoUtil.setEncryptionSrecret(config.appCredential.getApplicationSecret());
        CryptoUtil.setIv(iv);
        String encryptedText = encrypt(testData);
        assertNotNull(encryptedText);
        String result = decrypt(encryptedText);
        assertTrue(result.equalsIgnoreCase(testData));

        // Let us try with unencryptet text
        String result2 = decrypt(testData);
        assertTrue(result2.equalsIgnoreCase(testData));

        String sharedKey = Hex.encodeHexString(UUID.randomUUID().toString().getBytes()).substring(0, 32);
        CryptoUtil.setEncryptionSrecret(sharedKey);
        CryptoUtil.setIv(iv);   // Weakness - these has to be set "atomically"
        String result3 = decrypt(encryptedText);
        assertTrue(result3.equalsIgnoreCase(testData));

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
