package net.whydah.sso.util;


import net.whydah.sso.config.ApplicationMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class SSLTool {


    private static final Logger log = LoggerFactory.getLogger(SSLTool.class);
    private static final String CACERTS_PATH = "/lib/security/cacerts";
    private static final String CACERTS_PASSWORD = "changeit";
    public static SSLContext sc = null;
    public static SSLSocketFactory TRUSTED_FACTORY;
    public static HostnameVerifier hv;
    private static SSLSocketFactory sslSocketFactory;

    static {
        readCertificates();
    }

    public static boolean isCertificateCheckDisabled() {
        return sc != null;
    }

    public static void disableCertificateValidation() {

    	//REMOVE HERE FOR PROD TYPE
    	if(ApplicationMode.getApplicationMode().equals(ApplicationMode.PROD)){
    		return;
    	}
    	
        log.warn("Installing a trust manager which does not validate SSL/TLS certificates, DO NOT USE IN PRODUCTION!!");
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }


                }};

        // Ignore differences between given hostname and certificate hostname
        hv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting trust manager
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            TRUSTED_FACTORY = sc.getSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            //context.init( new KeyManager[0], tm, new SecureRandom( ) );

            sslSocketFactory = (SSLSocketFactory) sc.getSocketFactory();
        } catch (Exception e) {
        }
    }


    /**
     * Add a certificate to the cacerts keystore if it's not already included
     *
     * @param alias           The alias for the certificate, if added
     * @param certInputStream The certificate input stream
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */
    public static void ensureSslCertIsInKeystore(String alias, InputStream certInputStream)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        //get default cacerts file
        final File cacertsFile = new File(System.getProperty("java.home") + CACERTS_PATH);
        if (!cacertsFile.exists()) {
            throw new FileNotFoundException(cacertsFile.getAbsolutePath());
        }

        //load cacerts keystore
        FileInputStream cacertsIs = new FileInputStream(cacertsFile);
        final KeyStore cacerts = KeyStore.getInstance(KeyStore.getDefaultType());
        cacerts.load(cacertsIs, CACERTS_PASSWORD.toCharArray());
        cacertsIs.close();

        //load certificate from input stream
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        final Certificate cert = cf.generateCertificate(certInputStream);
        certInputStream.close();

        //check if cacerts contains the certificate
        if (cacerts.getCertificateAlias(cert) == null) {
            //cacerts doesn't contain the certificate, add it
            cacerts.setCertificateEntry(alias, cert);
            //write the updated cacerts keystore
            FileOutputStream cacertsOs = new FileOutputStream(cacertsFile);
            cacerts.store(cacertsOs, CACERTS_PASSWORD.toCharArray());
            cacertsOs.close();
        }
    }


    private static void readCertificates() {
        try {
            loadFromClasspath("ca.crt");
            loadFromFile("ca.crt");
        } catch (Exception e) {
        }
        try {
            loadFromClasspath("sub.class1.server.ca.crt");
            loadFromFile("sub.class1.server.ca.crt");
        } catch (Exception e) {
        }
        try {
            loadFromClasspath("sub.class2.server.ca.crt");
            loadFromFile("sub.class2.server.ca.crt");
        } catch (Exception e) {
        }
        try {
            loadFromClasspath("sub.class3.server.ca.crt");
            loadFromFile("sub.class3.server.ca.crt");
        } catch (Exception e) {
        }
        try {
            loadFromClasspath("sub.class4.server.ca.crt");
            loadFromFile("sub.class4.server.ca.crt");
        } catch (Exception e) {
        }
    }

    private static void loadFromClasspath(String certFile) throws Exception {
        log.info("Loading certificate from classpath: {}", certFile);
        InputStream is = SSLTool.class.getClassLoader().getResourceAsStream(certFile);
        if (is == null) {
            log.error("Error reading {} from classpath.", certFile);
        }
        SSLTool.ensureSslCertIsInKeystore("startssl-" + certFile, is);
        is.close();
    }

    private static void loadFromFile(String certFile) throws Exception {
        try {
            InputStream is = new FileInputStream(certFile);
            if (is != null) {
                SSLTool.ensureSslCertIsInKeystore("startssl-" + certFile, is);
            }
            is.close();
        } catch (Exception e) {
            log.error("Error reading sub.class4.server.ca.crt from classpath.");
        }

    }

}