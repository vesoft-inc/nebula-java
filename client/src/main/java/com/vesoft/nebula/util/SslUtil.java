/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.util;

import com.vesoft.nebula.client.graph.data.CASignedSSLParam;
import com.vesoft.nebula.client.graph.data.SelfSignedSSLParam;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SslUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SslUtil.class);

    private static TrustManager[] trustManagers;

    public static SSLSocketFactory getSSLSocketFactoryWithCA(CASignedSSLParam param) {
        // skip the verify for server certificate
        if (param.isSkipVerifyServer()) {
            return getSSLSocketFactoryWithoutVerify();
        }

        // if cert and key are null, consider the server certificate is CA,
        // and verify if server certificate is CA.
        if (param.getCaCrtFilePath() == null) {
            return getSSLSocketFactoryVerifyCACert();
        }


        // verify server certificate using client config, the server certificate must be sign
        // by client caCrt.
        final String caCrtFile = param.getCaCrtFilePath();
        final String crtFile = param.getCrtFilePath();
        final String keyFile = param.getKeyFilePath();
        final String password = "";
        try {
            //Add BouncyCastle as a Security Provider
            Security.addProvider(new BouncyCastleProvider());

            // Load client private key
            PEMParser reader = null;
            Object keyObject;
            try {
                reader = new PEMParser(new FileReader(keyFile));
                keyObject = reader.readObject();
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }

            PEMDecryptorProvider provider =
                    new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
            JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter().setProvider("BC");

            KeyPair key;

            if (keyObject instanceof PEMEncryptedKeyPair) {
                key = keyConverter.getKeyPair(((PEMEncryptedKeyPair) keyObject)
                        .decryptKeyPair(provider));
            } else {
                key = keyConverter.getKeyPair((PEMKeyPair) keyObject);
            }

            // Load Certificate Authority (CA) certificate
            X509CertificateHolder caCertHolder;
            try {
                reader = new PEMParser(new FileReader(caCrtFile));
                caCertHolder = (X509CertificateHolder) reader.readObject();
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }

            // CA certificate is used to authenticate server
            JcaX509CertificateConverter certificateConverter =
                    new JcaX509CertificateConverter().setProvider("BC");
            KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            X509Certificate caCert = certificateConverter.getCertificate(caCertHolder);
            caKeyStore.load(null, null);
            caKeyStore.setCertificateEntry("ca-certificate", caCert);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(caKeyStore);

            // Load client certificate
            X509CertificateHolder certHolder;
            try {
                reader = new PEMParser(new FileReader(crtFile));
                certHolder = (X509CertificateHolder) reader.readObject();
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }

            // Client key and certificates are sent to server so it can authenticate the client
            KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            X509Certificate cert = certificateConverter.getCertificate(certHolder);
            clientKeyStore.load(null, null);
            clientKeyStore.setCertificateEntry("certificate", cert);
            clientKeyStore.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(),
                    new Certificate[]{cert});

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());

            // Create SSL socket factory
            SSLContext context = SSLContext.getInstance("TLSv1.3");
            context.init(keyManagerFactory.getKeyManagers(),
                    trustManagerFactory.getTrustManagers(), null);


            trustManagers = trustManagerFactory.getTrustManagers();
            // Return the newly created socket factory object
            return context.getSocketFactory();

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return null;
    }

    public static SSLSocketFactory getSSLSocketFactoryWithoutCA(SelfSignedSSLParam param) {
        final String crtFile = param.getCrtFilePath();
        final String keyFile = param.getKeyFilePath();
        final String password = param.getPassword();
        try {
            // Add BouncyCastle as a Security Provider
            Security.addProvider(new BouncyCastleProvider());

            // Load client private key
            PEMParser reader = null;
            Object keyObject;
            try {
                reader = new PEMParser(new FileReader(keyFile));
                keyObject = reader.readObject();
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }

            PEMDecryptorProvider provider =
                    new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
            JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter().setProvider("BC");

            KeyPair key;

            if (keyObject instanceof PEMEncryptedKeyPair) {
                key = keyConverter.getKeyPair(((PEMEncryptedKeyPair) keyObject)
                        .decryptKeyPair(provider));
            } else {
                key = keyConverter.getKeyPair((PEMKeyPair) keyObject);
            }

            // certificate is used to authenticate server
            JcaX509CertificateConverter certificateConverter =
                    new JcaX509CertificateConverter().setProvider("BC");

            // Load client certificate
            X509CertificateHolder certHolder;
            try {
                reader = new PEMParser(new FileReader(crtFile));
                certHolder = (X509CertificateHolder) reader.readObject();
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }

            X509Certificate cert = certificateConverter.getCertificate(certHolder);

            // certificate is used to authenticate server
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            System.out.println(keyStore);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("certificate", cert);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            // Client key and certificates are sent to server so it can authenticate the client
            KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            clientKeyStore.load(null, null);
            clientKeyStore.setCertificateEntry("certificate", cert);
            clientKeyStore.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(),
                    new Certificate[]{cert});

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());

            // Create SSL socket factory
            SSLContext context = SSLContext.getInstance("TLSv1.3");
            context.init(keyManagerFactory.getKeyManagers(),
                    trustManagerFactory.getTrustManagers(), null);

            trustManagers = trustManagerFactory.getTrustManagers();
            // Return the newly created socket factory object
            return context.getSocketFactory();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static TrustManager[] getTrustManagers() {
        return trustManagers;
    }


    public static SSLSocketFactory getSSLSocketFactoryWithoutVerify() {
        TrustManager[] trustManagers = new TrustManager[]{
            new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    /**
     * check if the server's cert is issued by a CA
     */
    public static SSLSocketFactory getSSLSocketFactoryVerifyCACert() {
        try {
            String trustStoreUrl = System.getProperty("javax.net.ssl.trustStore");
            String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

            if (trustStoreUrl == null || trustStorePassword == null) {
                throw new RuntimeException(
                        "No truststore provided to verify the Server certificate,"
                                + " please set javax.net.ssl.trustStore and "
                                + "javax.net.ssl.trustStorePassword for the System.");
            }
            InputStream in = new FileInputStream(trustStoreUrl);
            keystore.load(in, trustStorePassword.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, trustStorePassword.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(keystore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            LOGGER.error("get SSLSocketFactory error,", e);
        }
        return null;
    }
}
