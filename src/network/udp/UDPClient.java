package network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;

    public boolean connectToServer(String host, int port) {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName(host);
            this.port = port;
            System.out.println("UDP Client verbonden met " + host + " op poort " + port);
            return true;
        } catch (IOException e) {
            System.err.println("UDP Client verbinding mislukt: " + e.getMessage());
            return false;
        }
    }

    public void sendMessage(String message) {
        try {
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(packet);
            System.out.println("UDP Client heeft bericht verzonden: " + message);
        } catch (IOException e) {
            System.err.println("UDP Client kon bericht niet verzenden: " + e.getMessage());
        }
    }

    public String receiveMessage() {
        try {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("UDP Client heeft bericht ontvangen: " + received);
            return received;
        } catch (IOException e) {
            System.err.println("UDP Client kon bericht niet ontvangen: " + e.getMessage());
            return null;
        }
    }

    public void close() {
        socket.close();
        System.out.println("UDP Client verbinding gesloten.");
    }
}
