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

    public boolean connectToServer(String host, int port, String sharedSecretKey) {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName(host);
            this.port = port;
            this.secretKey = new SecretKeySpec(sharedSecretKey.getBytes(), "HmacSHA256");
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
            byte[] mac = generateHMAC(buffer);
            byte[] combined = new byte[buffer.length + mac.length];
            System.arraycopy(buffer, 0, combined, 0, buffer.length);
            System.arraycopy(mac, 0, combined, buffer.length, mac.length);
            
            DatagramPacket packet = new DatagramPacket(combined, combined.length, address, port);
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

    private byte[] generateHMAC(byte[] message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            return mac.doFinal(message);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            System.err.println("Fout bij het genereren van HMAC: " + e.getMessage());
            return null;
        }
    }
}
