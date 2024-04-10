package network.udp;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private SecretKeySpec secretKey;

    // Method to establish connection with the server
    public boolean connectToServer(String host, int port, String sharedSecretKey) {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName(host);
            this.port = port;
            this.secretKey = new SecretKeySpec(sharedSecretKey.getBytes(), "HmacSHA256");
            System.out.println("UDP Client connected to " + host + " on port " + port);
            return true;
        } catch (IOException e) {
            System.err.println("UDP Client connection failed: " + e.getMessage());
            return false;
        }
    }

    // Method to send a message to the server
    public void sendMessage(String message) {
        try {
            byte[] buffer = message.getBytes();
            byte[] mac = generateHMAC(buffer);
            byte[] combined = new byte[buffer.length + mac.length];
            System.arraycopy(buffer, 0, combined, 0, buffer.length);
            System.arraycopy(mac, 0, combined, buffer.length, mac.length);
            
            DatagramPacket packet = new DatagramPacket(combined, combined.length, address, port);
            socket.send(packet);
            System.out.println("UDP Client has sent message: " + message);
        } catch (IOException e) {
            System.err.println("UDP Client could not send message: " + e.getMessage());
        }
    }

    // Method to receive a message from the server
    public String receiveMessage() {
        try {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("UDP Client has received message: " + received);
            return received;
        } catch (IOException e) {
            System.err.println("UDP Client could not receive message: " + e.getMessage());
            return null;
        }
    }

    // Method to close the connection
    public void close() {
        socket.close();
        System.out.println("UDP Client connection closed.");
    }

    // Method to generate HMAC for message integrity
    private byte[] generateHMAC(byte[] message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            return mac.doFinal(message);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            System.err.println("Error generating HMAC: " + e.getMessage());
            return null;
        }
    }
}
