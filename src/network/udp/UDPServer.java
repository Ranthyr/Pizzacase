package network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import app.server.Server;

public class UDPServer implements Runnable {
    public final static int PORT = 5001;
    private static final int BUFFER_SIZE = 1024;

    private DatagramSocket socket;
    private byte[] buffer = new byte[BUFFER_SIZE];
    private Server server;

    public UDPServer() {
        try {
            socket = new DatagramSocket(PORT);
            server = Server.getInstance();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received from client: " + received);

                // Opslaan van de bestelling in de database
                server.saveOrder(received);

                // Respond to client
                byte[] responseMessage = "Order received. Thank you!".getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseMessage, responseMessage.length,
                        packet.getAddress(), packet.getPort());
                socket.send(responsePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        if (socket != null) {
            socket.close();
        }
    }
}
