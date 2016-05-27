package net.whydah.sso.util;

import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static junit.framework.TestCase.assertTrue;

public class ImportStartSSLCertificatesTest {

    @Test
    public void testImportCACert() {

        InputStream is;

        try {
            is = new FileInputStream("./src/main/resources/ca.crt");
            SSLTool.ensureSslCertIsInKeystore("resources/startssl-ca", is);
            is.close();

            is = new FileInputStream("./src/main/resources/sub.class1.server.ca.crt");
            SSLTool.ensureSslCertIsInKeystore("startssl-sub.class1", is);
            is.close();

            is = new FileInputStream("./src/main/resources/sub.class2.server.ca.crt");
            SSLTool.ensureSslCertIsInKeystore("startssl-sub.class2", is);
            is.close();

            is = new FileInputStream("./src/main/resources/sub.class3.server.ca.crt");
            SSLTool.ensureSslCertIsInKeystore("startssl-sub.class3", is);
            is.close();

            is = new FileInputStream("./src/main/resources/sub.class4.server.ca.crt");
            SSLTool.ensureSslCertIsInKeystore("startssl-sub.class4", is);
            is.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Ignore
    @Test
    public void testSSSLToolImportCACert() throws Exception {
        SSLTool.disableCertificateValidation();
        assertTrue(SSLTool.ensureSslCertIsInKeystore("startssl-sub.class4"));
    }

}
