package network.tcp;

import javax.net.ssl.*;
import app.server.Server;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

public class TCPSSLServer implements Runnable {
    private static final int PORT = 5000;

    @Override
    public void run() {
        try {
            // Loading the keystore for SSL configuration
            KeyStore serverKeyStore = KeyStore.getInstance("JKS");
            char[] password = "PizzaCase".toCharArray();
            try (FileInputStream fis = new FileInputStream("src/network/tcp/keystore.jks")) {
                serverKeyStore.load(fis, password);
            }

            // Initializing the KeyManagerFactory and SSLContext for SSL configuration
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(serverKeyStore, password);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            // Configuring the SSLServerSocket
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(PORT);
            System.out.println("TCP SSL Server started on port " + PORT);

            // Accepting client connections
            while (true) {
                try {
                    SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket);

                    new Thread(() -> {
                        try {
                            handleClient(clientSocket);
                        } catch (IOException e) {
                            System.err.println("Error during communication with client: " + e.getMessage());
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                System.err.println("Error closing client socket: " + e.getMessage());
                            }
                        }
                    }).start();
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException | UnrecoverableKeyException e) {
            System.err.println("Server startup error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClient(SSLSocket clientSocket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            StringBuilder messageBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("EOF")) {
                    break;
                }
                messageBuilder.append(line).append("\n");
            }

            String receivedMessage = messageBuilder.toString();
            System.out.println("Received complete message from client: " + receivedMessage);

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
                    System.out.println("Order successfully saved to database.");
                    out.println("Order confirmed: " + receivedMessage);
                } else {
                    System.err.println("Order could not be saved to database.");
                    out.println("Order could not be saved: " + receivedMessage);
                }
            } else {
                System.err.println("Invalid order received.");
                out.println("Invalid order received.");
            }
        }
    }
}
