/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.util;

import com.vesoft.nebula.client.graph.data.CASignedSSLParam;
import com.vesoft.nebula.client.graph.data.SelfSignedSSLParam;
import java.io.FileReader;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
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

    public static SSLSocketFactory getSSLSocketFactoryWithCA(CASignedSSLParam param) {
        final String caCrtFile = param.getCaCrtFilePath();
        final String crtFile = param.getCrtFilePath();
        final String keyFile = param.getKeyFilePath();
        final String password = "";
        try {
            //Add BouncyCastle as a Security Provider
            Security.addProvider(new BouncyCastleProvider());

            // Load client private key
            PEMParser reader = new PEMParser(new FileReader(keyFile));
            Object keyObject = reader.readObject();
            reader.close();

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
            reader = new PEMParser(new FileReader(caCrtFile));
            X509CertificateHolder caCertHolder = (X509CertificateHolder) reader.readObject();
            reader.close();

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
            reader = new PEMParser(new FileReader(crtFile));
            X509CertificateHolder certHolder = (X509CertificateHolder) reader.readObject();
            reader.close();

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
            PEMParser reader = new PEMParser(new FileReader(keyFile));
            Object keyObject = reader.readObject();
            reader.close();

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
            reader = new PEMParser(new FileReader(crtFile));
            X509CertificateHolder certHolder = (X509CertificateHolder) reader.readObject();
            reader.close();

            X509Certificate cert = certificateConverter.getCertificate(certHolder);
            // certificate is used to authenticate server
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
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

            // Return the newly created socket factory object
            return context.getSocketFactory();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return null;
    }
}
