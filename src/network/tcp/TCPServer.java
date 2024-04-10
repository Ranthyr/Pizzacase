package network.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import app.server.Server;

public class TCPServer implements Runnable {
    private static final int PORT = 5000;

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("TCP Server gestart op poort " + PORT);
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client verbonden: " + clientSocket);

                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    // Ontvangen van client
                    StringBuilder orderBuilder = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        orderBuilder.append(line).append("\n");
                    }
                    String order = orderBuilder.toString().trim();
                    System.out.println("Ontvangen van client: " + order);

                    // Split de ontvangen order
                    String[] orderDetails = order.split("\n");
                    if (orderDetails.length >= 6) {
                        String customerName = orderDetails[0];
                        String address = orderDetails[1];
                        String orderTime = orderDetails[orderDetails.length - 1]; // Tijd is de laatste regel
                        StringBuilder orderDescriptionBuilder = new StringBuilder();
                        for (int i = 2; i < orderDetails.length - 1; i++) {
                            orderDescriptionBuilder.append(orderDetails[i]).append("\n");
                        }
                        String orderDescription = orderDescriptionBuilder.toString().trim();

                        // Server gebruiken om de bestelling op te slaan
                        System.out.println("Probeer bestelling op te slaan in database: " + order);
                        Server.getInstance().saveOrderToDatabase(customerName, address, orderDescription, orderTime);
                        System.out.println("Bestelling succesvol opgeslagen in de database.");

                        // Verwerk en reageer op bericht
                        String response = "Order bevestigd: " + order;
                        out.println(response);

                        System.out.println("Respons verzonden naar client.");
                    } else {
                        System.err.println("Ongeldige bestelling ontvangen.");
                    }
                } catch (IOException e) {
                    System.err.println("Fout bij het communiceren met de client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Kan geen TCP Server starten op poort " + PORT + ": " + e.getMessage());
        }
    }
}
