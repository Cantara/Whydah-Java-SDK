package net.whydah.sso.util;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImportStartSSLCertificatesTest {

    @Test
    public void testImportCACert() {

        InputStream is;

        try {
            is = new FileInputStream("./src/resources/ca.crt");

            SSLTool.ensureSslCertIsInKeystore("startssl-ca", is);
            is.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
