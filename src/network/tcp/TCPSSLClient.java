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
    private SSLSocket socket; // Socket for SSL connection
    private PrintWriter out; // Writer for sending messages
    private BufferedReader in; // Reader for receiving messages

    // Method to establish connection with SSL to the server
    public boolean connectToServer(String host, int port) {
        try {
            // Load server's keystore
            KeyStore serverKeyStore = KeyStore.getInstance("JKS");
            char[] password = "PizzaCase".toCharArray();
            try (FileInputStream fis = new FileInputStream("src/network/tcp/keystore.jks")) {
                serverKeyStore.load(fis, password);
            }

            // Initialize key manager factory with server's keystore
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(serverKeyStore, password);

            // Load client's truststore
            KeyStore clientTrustStore = KeyStore.getInstance("JKS");
            try (FileInputStream fis = new FileInputStream("src/network/tcp/truststore.jks")) {
                clientTrustStore.load(fis, password);
            }

            // Initialize trust manager factory with client's truststore
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(clientTrustStore);

            // Initialize SSL context with key and trust managers
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

            // Get SSL socket factory from SSL context
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            // Connect to server
            System.out.println("Connecting to server at " + host + ":" + port);
            socket = (SSLSocket) sslSocketFactory.createSocket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to server.");
            return true;
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException | UnrecoverableKeyException e) {
            handleIOException("Failed to connect to server.", e);
            return false;
        }
    }

    // Method to send a message to the server
    public void sendMessage(String message) {
        if (out == null) {
            System.err.println("Connection to server has not been established.");
            return;
        }

        // Append EOF marker to the end of each message
        message += "\nEOF\n";
        System.out.println("Sending message: " + message);
        out.println(message);
    }

    // Method to receive a response from the server
    public String getResponse() {
        if (in == null) {
            System.err.println("Connection to server has not been established.");
            return null;
        }

        try {
            System.out.println("Waiting for response from server...");
            return in.readLine();
        } catch (IOException e) {
            handleIOException("Error receiving response.", e);
            return null;
        }
    }

    // Method to close the connection
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
            System.out.println("Connection closed.");
        } catch (IOException e) {
            handleIOException("Error closing connection.", e);
        }
    }

    // Method to handle IOException
    private void handleIOException(String message, Exception e) {
        System.err.println(message + " Error: " + e.getMessage());
        e.printStackTrace();
    }
}
