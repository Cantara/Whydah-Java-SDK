package net.whydah.sso.session.baseclasses;

import org.slf4j.Logger;

import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

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

    public ExchangeableKey(String exportedString) {
        log.debug("Constructor called with " + exportedString);
        this.encryptionKey = encryptionKey;
        this.iv = iv;
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

    @Override
    public String toString() {
        return "ExchangeableKey{" +
                "encryptionKey=" + encoder.encodeToString(encryptionKey) +
                ", iv=" + encoder.encodeToString(iv.getIV()) +
                '}';
    }
}
