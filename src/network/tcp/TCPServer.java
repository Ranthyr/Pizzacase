package network.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable {
    private static final int PORT = 5000; // Aangepast van 0 naar een specifieke poort

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("TCP Server gestart op poort " + PORT);
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client verbonden: " + clientSocket);

                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    
                    String order = in.readLine();
                    System.out.println("Ontvangen van client: " + order);
                    
                    // Verwerk en reageer op bericht
                    String response = "Order bevestigd: " + order;
                    out.println(response);
                    
                    System.out.println("Respons verzonden naar client.");
                } catch (IOException e) {
                    System.err.println("Fout bij het communiceren met de client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Kan geen TCP Server starten op poort " + PORT + ": " + e.getMessage());
        }
    }
}
