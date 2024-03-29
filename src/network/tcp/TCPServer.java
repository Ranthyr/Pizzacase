package network.tcp;

import app.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable {
    public static final int PORT = 0;

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("TCP Server started on port 5000");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);
                try (ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream())) {
                    String order = (String) inputStream.readObject();
                    System.out.println("Received from client: " + order);
                    // Opslaan in database
                    Server.getInstance().saveOrder(order);
                    // Stuur bevestiging naar client
                    // (Dit kan verder worden uitgewerkt afhankelijk van de behoeften van de applicatie)
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}