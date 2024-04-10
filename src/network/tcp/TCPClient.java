package network.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public boolean connectToServer(String host, int port) {
        try {
            System.out.println("Verbinden met server op " + host + ":" + port);
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Verbonden met server.");
            return true;
        } catch (IOException e) {
            handleIOException("Kan niet verbinden met server.", e);
            return false;
        }
    }

    public void sendMessage(String message) {
        if (out == null) {
            System.err.println("Verbinding met de server is niet tot stand gebracht.");
            return;
        }

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
            String response = in.readLine();
            System.out.println("Respons ontvangen: " + response);
            return response;
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

    private void handleIOException(String message, IOException e) {
        System.err.println(message + " Error: " + e.getMessage());
        e.printStackTrace(); // Print de stacktrace voor gedetailleerde foutinformatie
    }
}
