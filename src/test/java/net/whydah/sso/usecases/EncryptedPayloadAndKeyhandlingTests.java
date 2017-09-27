package net.whydah.sso.usecases;

import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.basehelpers.JsonPathHelper;
import net.whydah.sso.session.baseclasses.CryptoUtil;
import net.whydah.sso.session.baseclasses.ExchangeableKey;
import net.whydah.sso.util.StringConv;
import net.whydah.sso.util.SystemTestBaseConfig;
import org.apache.commons.codec.binary.Hex;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
import static net.whydah.sso.util.LoggerUtil.first50;
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

        CryptoUtil.setEncryptionSecretAndIv(config.appCredential.getApplicationSecret(), iv);
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
        CryptoUtil.setEncryptionSecretAndIv(sharedKey, new IvParameterSpec("01234567890ABCDE".getBytes()));
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
        //assertTrue(jsonEncodedKey.equalsIgnoreCase(ekey.toJsonEncoded()));
        log.debug("{}", first50(ekey.toJsonEncoded()));
        ekey.setIv(new IvParameterSpec("01234567890ABCDE".getBytes()));

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

    @Test
    public void testRealPayload() throws Exception {
        String payload = "<applicationtoken>\n" +
                "     <params>\n" +
                "         <applicationtokenID>a9cc89d942ac4af8a9123fcdbc6fa911</applicationtokenID>\n" +
                "         <applicationid>9999</applicationid>\n" +
                "         <applicationname>Whydah-Jenkins</applicationname>\n" +
                "         <expires>1506107910829</expires>\n" +
                "     </params> \n" +
                "     <Url type=\"application/xml\" method=\"POST\"                 template=\"https://whydahdev.cantara.no/tokenservice/user/a9cc89d942ac4af8a9123fcdbc6fa911/get_usertoken_by_usertokenid\"/> \n" +
                " </applicationtoken>";

        CryptoUtil.setEncryptionSecretAndIv(config.appCredential.getApplicationSecret(), new IvParameterSpec("01234567890ABCDE".getBytes()));
        String encryptedText = encrypt(payload);
        assertNotNull(encryptedText);
        String result = decrypt(StringConv.UTF8(encryptedText));
        assertTrue(result.equalsIgnoreCase(payload));

//        String cryptoText="4cef1766d14c378a5d73f1a6a5df78a3cc38e10ae55b9dc9c140f18ea2e8d7b8c1d174e37adbbaf4235b2a260d6229c5033e2534fcfbb4c06d1a976830256c7dec20c02f3521c24adb0b9979ffc0c889270ab951f42d2ac1d3a1433d9c80da3e0fdf79c275b00929ced21995b70eb5dadb240e1882a7efeb4a1037191d2f9f5f8e7231c52067aa58cbe7fd11df9f11920a57a1559483c550eb326cb1f310367f74c86988e9696b9efb9743fdc2af77c83095eb917c505f70ed1ea68a89f5f489878f12dec868c146bd4f5ca94ee9bc7574d7ed35b72b52023ded8e1271a31cbd246426b48d15450ecca0c80cb1646bb9ef59c5bd626dda2a91f8289444551b4aace96162bf25ce596e77e57b755835d0d8f43e4e479d037c485f4c2da670e6ea858d6dceff1321f1328d0120aa575b92a38e3eaad1968023fbdf5730a8f6d0a87dc3a8167d9fe163cf0ead3c7e769f44022645a74352a3e94e0c3736cdfe01cd7b03585fa51133e968ea9e04ffcbe67502c1b7fa3110461c53dbe5ff81d31c87ec45675322d8d29033dad0e7ea2b4c8a247d11dde6cb73c7aaffc58b0d5ed421b116bb2fbb143d9b9f0e01a96c88c330b517b7678120db2a23f7f186aa0a1ecb8cb89cdbbbe8bdd6027c70b2b52011b493b4621914690a732436002658cce4b8e24bf88b37f058a7e6c379ca0eec6205";
        String cryptoText = "a5d9941b3e5883e946b3e6930bbb3b571cbf6bd9eab5a290fd938b330305fa494b93dd65c262fedc8e4baa06f903bf13b6d98a6a53e7c945389cc357f66df4efbcbff903de5f1e0de4cb45f93346b60626f7a1284323c23528d9688ed77f8632b7648db549880c1217ac7294efd34efacb345d8258177916a7c35f3d0a92e03b0afe00a3b1c71e9a318f136ba7d617986aeb6707350604ba26b0513fb291936df5ae81500c086e5a0acb436bac3df1934f83e8005742c8d94811d00d24ad5d4f934e23e3200a4367a521b239065a48d478520a2b240e7dfff4231ebc1b14830af1ed37bb4686704d2f2aab4477ff61b5d661273952bb27e99f37e16b4e916e1d64592342c9c374aed61ce0caed422efb0658238559e95f3ffa9a844a59dfc7467799af89327e2c374bb42f4092dd4f8d31c3781e770d7654e9080a506095ba39d89d841b0b4520442b54506c28c9b516dd4d74f91161e55455202d2ca49e8132648bb762af6364977fcd899d6dfe33cea5d6ce19410d57a3996f0f3a57ebd7ec06448564e1e6bcd73ff5c6c3e71ce84134a6311e20c512b54df309f8caf6ce5b83b38658235c012b405e8adfeacb00f8a10fe833a93be8dd23135491e5d071de7853e6794953aa1fa9315428560f4666afbcab51851c90f04ee555c1b5f86c7c07fe2e192d7285df699145cb85f2a336";
//        "a5d9941b3e5883e946b3e6930bbb3b571cbf6bd9eab5a290fd938b330305fa494b93dd65c262fedc8e4baa06f903bf13b6d98a6a53e7c945389cc357f66df4efbcbff903de5f1e0de4cb45f93346b60626f7a1284323c23528d9688ed77f8632b7648db549880c1217ac7294efd34efacb345d8258177916a7c35f3d0a92e03b0afe00a3b1c71e9a318f136ba7d617986aeb6707350604ba26b0513fb291936df5ae81500c086e5a0acb436bac3df1934f83e8005742c8d94811d00d24ad5d4f934e23e3200a4367a521b239065a48d478520a2b240e7dfff4231ebc1b14830af1ed37bb4686704d2f2aab4477ff61b5d661273952bb27e99f37e16b4e916e1d64592342c9c374aed61ce0caed422efb0658238559e95f3ffa9a844a59dfc7467799af89327e2c374bb42f4092dd4f8d31c3781e770d7654e9080a506095ba39d89d841b0b4520442b54506c28c9b516dd4d74f91161e55455202d2ca49e8132648bb762af6364977fcd899d6dfe33cea5d6ce19410d57a3996f0f3a57ebd7ec06448564e1e6bcd73ff5c6c3e71ce84134a6311e20c512b54df309f8caf6ce5b83b38658235c012b405e8adfeacb00f8a10fe833a93be8dd23135491e5d071de7853e6794953aa1fa9315428560f4666afbcab51851c90f04ee555c1b5f86c7c07fe2e192d7285df699145cb85f2a336"
        String result2 = decrypt(StringConv.UTF8(encryptedText));
        ApplicationToken applicationToken = ApplicationTokenMapper.fromXml(result2);
        assertTrue(applicationToken != null);
    }

    @Test
    @Ignore
    public void testCryptoSecretFunctiion() throws Exception {
        String payload = "<applicationtoken>\n" +
                "     <params>\n" +
                "         <applicationtokenID>a9cc89d942ac4af8a9123fcdbc6fa911</applicationtokenID>\n" +
                "         <applicationid>9999</applicationid>\n" +
                "         <applicationname>Whydah-Jenkins</applicationname>\n" +
                "         <expires>1506107910829</expires>\n" +
                "     </params> \n" +
                "     <Url type=\"application/xml\" method=\"POST\"                 template=\"https://whydahdev.cantara.no/tokenservice/user/a9cc89d942ac4af8a9123fcdbc6fa911/get_usertoken_by_usertokenid\"/> \n" +
                " </applicationtoken>";

        CryptoUtil.setEncryptionSecretAndIv(config.appCredential.getApplicationSecret(), new IvParameterSpec("01234567890ABCDE".getBytes()));
        String encryptedText = encrypt(payload);
        CryptoUtil.setEncryptionSecretAndIv(config.appCredential.getApplicationSecret(), new IvParameterSpec("01234567890ABCDE".getBytes()));
        CryptoUtil.setEncryptionSecretAndIv(config.appCredential.getApplicationSecret(), new IvParameterSpec("01234567890ABCDE".getBytes()));
        assertNotNull(encryptedText);
        String result = decrypt(StringConv.UTF8(encryptedText));
        assertTrue(result.equalsIgnoreCase(payload));
    }

    @Test
    public void testCryptoKeyKeyHandling() throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        ExchangeableKey exchangeableKey = new ExchangeableKey();
        String iv = "MDEyMzQ1Njc4OTBBQkNERQ==";
        ExchangeableKey applicationKey = new ExchangeableKey();
        applicationKey.setEncryptionSecret(UUID.randomUUID().toString());
        applicationKey.setIv(new IvParameterSpec(decoder.decode(iv)));

        ExchangeableKey exchangeableKey1 = new ExchangeableKey();
        exchangeableKey1.setEncryptionSecret(UUID.randomUUID().toString());
        exchangeableKey1.setIv(new IvParameterSpec(decoder.decode(iv)));

        CryptoUtil.setExchangeableKey(applicationKey);
        CryptoUtil.setExchangeableKey(exchangeableKey1);
        assertTrue(exchangeableKey1.toJsonEncoded().equalsIgnoreCase(CryptoUtil.getActiveKey()));

    }
}
