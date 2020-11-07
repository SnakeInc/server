package de.uol.snakeinc.server.server;

import de.uol.snakeinc.server.connection.WebSocketServer;
import de.uol.snakeinc.server.game.GameHandler;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class WebSocketEnableWss {

    public WebSocketServer initWebSocketServer(GameHandler gameHandler, int port) {
        WebSocketServer webSocketServer = new WebSocketServer(gameHandler, port);
        SSLContext sslContext = getContext();
        if (sslContext != null) {
            webSocketServer.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(getContext()));
        }
        webSocketServer.setConnectionLostTimeout(15);
        return webSocketServer;
    }

    private static SSLContext getContext() {
        SSLContext context;
        String password = "";
        String pathname = "C:\\Apache24\\conf\\ssl\\wss";
        try {
            context = SSLContext.getInstance("TLS");

            byte[] certBytes = parseDERFromPEM(Files.readAllBytes(new File(pathname + File.separator + "pubcert.pem").toPath()),
                "-----BEGIN CERTIFICATE-----", "-----END CERTIFICATE-----");
            byte[] keyBytes = parseDERFromPEM(Files.readAllBytes(new File(pathname + File.separator + "privkey.pem").toPath()),
                "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");
            byte[] chainBytes = parseDERFromPEM(Files.readAllBytes(new File(pathname + File.separator + "pubchain.pem").toPath()),
                "-----BEGIN CERTIFICATE-----", "-----END CERTIFICATE-----");

            X509Certificate cert = generateCertificateFromDER(certBytes);
            X509Certificate chain = generateCertificateFromDER(chainBytes);
            RSAPrivateKey key = generatePrivateKeyFromDER(keyBytes);

            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(null);
            keystore.setCertificateEntry("cert-alias", cert);
            keystore.setCertificateEntry("cert-alias", chain);
            keystore.setKeyEntry("key-alias", key, password.toCharArray(), new Certificate[]{cert, chain});

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, password.toCharArray());

            KeyManager[] km = kmf.getKeyManagers();

            context.init(km, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            context = null;
        }
        return context;
    }

    private static byte[] parseDERFromPEM(byte[] pem, String beginDelimiter, String endDelimiter) {
        String data = new String(pem);
        String[] tokens = data.split(beginDelimiter);
        tokens = tokens[1].split(endDelimiter);
        return DatatypeConverter.parseBase64Binary(tokens[0]);
    }

    private static RSAPrivateKey generatePrivateKeyFromDER(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey) factory.generatePrivate(spec);
    }

    private static X509Certificate generateCertificateFromDER(byte[] certBytes) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
    }
}
