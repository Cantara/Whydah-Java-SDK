package net.whydah.sso.usecases;

import net.whydah.sso.basehelpers.JsonPathHelper;
import net.whydah.sso.session.baseclasses.CryptoUtil;
import net.whydah.sso.session.baseclasses.ExchangeableKey;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.apache.commons.codec.binary.Hex;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;

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
import static org.slf4j.LoggerFactory.getLogger;

public class EncryptedPayloadAndKeyhandlingTests {
    private static final Logger log = getLogger(EncryptedPayloadAndKeyhandlingTests.class);

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

        CryptoUtil.setEncryptionSecretAndIv(config.appCredential.getApplicationSecret(), iv));
        String encryptedText = encrypt(testData);
        assertNotNull(encryptedText);
        String result = decrypt(encryptedText);
        assertTrue(result.equalsIgnoreCase(testData));

        String receivedKey = CryptoUtil.getActiveKey();
        ExchangeableKey exchangeableKey = new ExchangeableKey(receivedKey);
        CryptoUtil.setExchangeableKey(exchangeableKey);
        String resultEK = decrypt(encryptedText);
        assertTrue(resultEK.equalsIgnoreCase(testData));


        // Let us try with unencryptet text
        String result2 = decrypt(testData);
        assertTrue(result2.equalsIgnoreCase(testData));

        String sharedKey = Hex.encodeHexString(UUID.randomUUID().toString().getBytes()).substring(0, 32);
        CryptoUtil.setEncryptionSecretAndIv(sharedKey, new IvParameterSpec("01234567890ABCDEF".getBytes()));
        String result3 = decrypt(encryptedText);
        assertTrue(result3.equalsIgnoreCase(testData));


    }


    @Test
    public void testUnmashallingExchangableKey() throws Exception {
        String jsonEncodedKey = "{  \n" +
                "   \"encryptionKey\":\"EhFw9E7XeCdhvG1ovt68pYh+00mGLLNR+Gv0neyRccM=\",\n" +
                "   \"iv\":\"SPSK0jqxW7syp5nV0TQsGQ==\"\n" +
                "}";


        ExchangeableKey ekey = new ExchangeableKey();
        String encryptionKeyEncoded = JsonPathHelper.findJsonPathValue(jsonEncodedKey, "$.encryptionKey");
        String iv = JsonPathHelper.findJsonPathValue(jsonEncodedKey, "$.iv");
        Base64.Decoder decoder = Base64.getDecoder();

        ekey.setEncryptionKey(decoder.decode(encryptionKeyEncoded));
        ekey.setIv(new IvParameterSpec(decoder.decode(iv)));
        log.debug(ekey.toJsonEncoded());
        ekey.setIv(new IvParameterSpec("01234567890ABCDEF".getBytes()));

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
