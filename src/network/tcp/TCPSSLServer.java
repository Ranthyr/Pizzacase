package network.tcp;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.KeyManagementException;

import app.server.Server;

public class TCPSSLServer implements Runnable {
    private static final int PORT = 5000;

    @Override
    public void run() {
        try {
            KeyStore serverKeyStore = KeyStore.getInstance("JKS");
            char[] password = "PizzaCase".toCharArray();
            try (FileInputStream fis = new FileInputStream("src/network/tcp/keystore.jks")) {
                serverKeyStore.load(fis, password);
            }

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(serverKeyStore, password);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(PORT);
            System.out.println("TCP SSL Server gestart op poort " + PORT);

            while (true) {
                try (SSLSocket clientSocket = (SSLSocket) serverSocket.accept()) {
                    System.out.println("Client verbonden: " + clientSocket);

                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    StringBuilder messageBuilder = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.equals("EOF")) {
                            break; // Stop lezen wanneer EOF marker is gevonden
                        }
                        messageBuilder.append(line).append("\n");
                    }

                    String receivedMessage = messageBuilder.toString();
                    System.out.println("Volledig bericht ontvangen van client: " + receivedMessage);

                    // Hieronder volgt de verwerking van het bericht zoals je oorspronkelijk had
                    String[] orderDetails = receivedMessage.split("\n");
                    if (orderDetails.length >= 6) {
                        String customerName = orderDetails[0];
                        String street = orderDetails[1];
                        String city = orderDetails[2];
                        String postalCode = orderDetails[3];
                        StringBuilder orderDescriptionBuilder = new StringBuilder();
                        for (int i = 4; i < orderDetails.length - 1; i++) {
                            orderDescriptionBuilder.append(orderDetails[i]).append("\n");
                        }
                        String orderDescription = orderDescriptionBuilder.toString().trim();
                        String orderTime = orderDetails[orderDetails.length - 1];

                        boolean savedSuccessfully = Server.getInstance().saveOrderToDatabase(customerName, street, city, postalCode, orderDescription, orderTime);

                        if (savedSuccessfully) {
                            System.out.println("Bestelling succesvol opgeslagen in de database.");
                            out.println("Order bevestigd: " + receivedMessage);
                        } else {
                            System.err.println("Bestelling kon niet worden opgeslagen in de database.");
                            out.println("Order kon niet worden opgeslagen: " + receivedMessage);
                        }
                    } else {
                        System.err.println("Ongeldige bestelling ontvangen.");
                        out.println("Ongeldige bestelling ontvangen.");
                    }
                } catch (IOException e) {
                    System.err.println("Fout tijdens communicatie met client: " + e.getMessage());
                }
            }
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException | UnrecoverableKeyException e) {
            System.err.println("Server startfout: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
