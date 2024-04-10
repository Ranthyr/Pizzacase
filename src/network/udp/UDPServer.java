package network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer implements Runnable {
    public final static int PORT = 5001;
    private static final int BUFFER_SIZE = 1024;

    private DatagramSocket socket;
    private byte[] buffer = new byte[BUFFER_SIZE];

    public UDPServer() {
        try {
            socket = new DatagramSocket(PORT);
            System.out.println("UDP Server gestart op poort " + PORT);
        } catch (SocketException e) {
            System.err.println("UDP Server kon niet starten op poort " + PORT + ": " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("UDP Server heeft ontvangen van client: " + received);

                // Voer de actie uit gebaseerd op het ontvangen bericht
                // (bijv. opslaan van bestelling, etc.)
                
                // Reageer op de client
                String response = "Order bevestigd: Dank u!";
                byte[] responseBytes = response.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, packet.getAddress(), packet.getPort());
                socket.send(responsePacket);
                System.out.println("UDP Server heeft bevestiging verzonden.");
            } catch (IOException e) {
                System.err.println("UDP Server fout: " + e.getMessage());
            }
        }
    }

    public void close() {
        socket.close();
        System.out.println("UDP Server verbinding gesloten.");
    }
}
