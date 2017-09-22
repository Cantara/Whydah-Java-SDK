package net.whydah.sso.session.baseclasses;

import javax.crypto.spec.IvParameterSpec;
import java.util.Arrays;

public class ExchangeableKey {

    private byte[] encryptionKey;
    private IvParameterSpec iv;

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
                "encryptionKey=" + Arrays.toString(encryptionKey) +
                ", iv=" + iv +
                '}';
    }
}
