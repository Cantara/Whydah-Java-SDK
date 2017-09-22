package net.whydah.sso.session.baseclasses;

import net.whydah.sso.basehelpers.JsonPathHelper;
import org.slf4j.Logger;

import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

import static net.whydah.sso.util.LoggerUtil.first50;
import static org.slf4j.LoggerFactory.getLogger;

public class ExchangeableKey {
    private static final Logger log = getLogger(ExchangeableKey.class);


    private byte[] encryptionKey;
    private IvParameterSpec iv;
    Base64.Encoder encoder = Base64.getEncoder();

    public ExchangeableKey(byte[] encryptionKey, IvParameterSpec iv) {
        this.encryptionKey = encryptionKey;
        this.iv = iv;
    }

    public ExchangeableKey(String jsonEncodedKey) {
        log.debug("Constructor called with {}", first50(jsonEncodedKey));
        String encryptionKeyEncoded = JsonPathHelper.findJsonPathValue(jsonEncodedKey, "$.encryptionKey");
        String iv = JsonPathHelper.findJsonPathValue(jsonEncodedKey, "$.iv");
        Base64.Decoder decoder = Base64.getDecoder();

        setEncryptionKey(decoder.decode(encryptionKeyEncoded));
        setIv(new IvParameterSpec(decoder.decode(iv)));
    }

    public ExchangeableKey() {
    }

    public byte[] getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(byte[] encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public IvParameterSpec getIv() {
        return iv;
    }

    public void setIv(IvParameterSpec iv) {
        this.iv = iv;
    }

    public String toJsonEncoded() {
        return "{" +
                "\"encryptionKey\":\"" + encoder.encodeToString(encryptionKey) + "\",\n" +
                "\"iv\":\"" + encoder.encodeToString(iv.getIV()) + "\"" +
                '}';
    }
}
