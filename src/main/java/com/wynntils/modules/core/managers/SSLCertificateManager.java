package com.wynntils.modules.core.managers;

import com.wynntils.Reference;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class SSLCertificateManager {
    
    public static void registerCerts() {
        tryUseWindowsCertStore();
        addLEKeyStore();
    }

    private static void addLEKeyStore() {
        try {
            final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            Path ksPath = Paths.get(System.getProperty("java.home"),"lib", "security", "cacerts");
            keyStore.load(Files.newInputStream(ksPath), "changeit".toCharArray());
            final Map<String, Certificate> jdkTrustStore = Collections.list(keyStore.aliases()).stream().collect(Collectors.toMap(a -> a, (String alias) -> {
                try {
                    return keyStore.getCertificate(alias);
                } catch (KeyStoreException e) {
                    throw new UncheckedKeyStoreException(e);
                }
            }));

            final KeyStore leKS = KeyStore.getInstance(KeyStore.getDefaultType());
            final InputStream leKSFile = SSLCertificateManager.class.getResourceAsStream("/assets/wynntils/certs/lekeystore.jks");
            leKS.load(leKSFile, "supersecretpassword".toCharArray());
            final Map<String, Certificate> leTrustStore = Collections.list(leKS.aliases()).stream().collect(Collectors.toMap(a -> a, (String alias) -> {
                try {
                    return leKS.getCertificate(alias);
                } catch (KeyStoreException e) {
                    throw new UncheckedKeyStoreException(e);
                }
            }));

            final KeyStore mergedTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            mergedTrustStore.load(null, new char[0]);
            for (Map.Entry<String, Certificate> entry : jdkTrustStore.entrySet()) {
                mergedTrustStore.setCertificateEntry(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String , Certificate> entry : leTrustStore.entrySet()) {
                mergedTrustStore.setCertificateEntry(entry.getKey(), entry.getValue());
            }

            final TrustManagerFactory instance = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            instance.init(mergedTrustStore);
            final SSLContext tls = SSLContext.getInstance("TLS");
            tls.init(null, instance.getTrustManagers(), null);
            HttpsURLConnection.setDefaultSSLSocketFactory(tls.getSocketFactory());
            Reference.LOGGER.info("Added Lets Encrypt root certificates as additional trust");
        } catch (UncheckedKeyStoreException | KeyStoreException | IOException | NoSuchAlgorithmException |
                 CertificateException | KeyManagementException e) {
            Reference.LOGGER.error("Failed to load lets encrypt certificate. Expect problems");
            e.printStackTrace();
        }
    }

    private static void tryUseWindowsCertStore() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

        if (!osName.contains("win")) {
            return;
        }

        // Use the operating system cert store
        System.setProperty("javax.net.ssl.trustStoreType", "WINDOWS-ROOT");
    }

    private static class UncheckedKeyStoreException extends RuntimeException {
        public UncheckedKeyStoreException(Throwable cause) {
            super(cause);
        }
    }
}
