package network.tcp;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class TCPSSLClient {
    private SSLSocket socket;
    private PrintWriter out;
    private BufferedReader in;

    public boolean connectToServer(String host, int port) {
        try {
            KeyStore serverKeyStore = KeyStore.getInstance("JKS");
            char[] password = "PizzaCase".toCharArray();
            try (FileInputStream fis = new FileInputStream("src/network/tcp/keystore.jks")) {
                serverKeyStore.load(fis, password);
            }

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(serverKeyStore, password);

            KeyStore clientTrustStore = KeyStore.getInstance("JKS");
            try (FileInputStream fis = new FileInputStream("src/network/tcp/truststore.jks")) {
                clientTrustStore.load(fis, password);
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(clientTrustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            System.out.println("Verbinden met server op " + host + ":" + port);
            socket = (SSLSocket) sslSocketFactory.createSocket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Verbonden met server.");
            return true;
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException | UnrecoverableKeyException e) {
            handleIOException("Kan niet verbinden met server.", e);
            return false;
        }
    }

    public void sendMessage(String message) {
        if (out == null) {
            System.err.println("Verbinding met de server is niet tot stand gebracht.");
            return;
        }

        // Voeg de EOF marker toe aan het einde van elk bericht
        message += "\nEOF\n";
        System.out.println("Versturen bericht: " + message);
        out.println(message);
    }

    public String getResponse() {
        if (in == null) {
            System.err.println("Verbinding met de server is niet tot stand gebracht.");
            return null;
        }

        try {
            System.out.println("Wachten op respons van de server...");
            return in.readLine();
        } catch (IOException e) {
            handleIOException("Fout bij het ontvangen van de respons.", e);
            return null;
        }
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            System.out.println("Verbinding gesloten.");
        } catch (IOException e) {
            handleIOException("Fout bij het sluiten van de verbinding.", e);
        }
    }

    private void handleIOException(String message, Exception e) {
        System.err.println(message + " Error: " + e.getMessage());
        e.printStackTrace();
    }
}
