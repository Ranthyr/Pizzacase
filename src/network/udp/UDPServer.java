package network.udp;

import app.server.Server;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class UDPServer implements Runnable {
    public final static int PORT = 5001;
    private static final int BUFFER_SIZE = 1024;
    private final SecretKeySpec secretKey;

    private DatagramSocket socket;
    private byte[] buffer = new byte[BUFFER_SIZE];

    public UDPServer(String sharedSecretKey) {
        this.secretKey = new SecretKeySpec(sharedSecretKey.getBytes(), "HmacSHA256");

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
                byte[] message = Arrays.copyOfRange(packet.getData(), 0, packet.getLength() - 32);
                byte[] receivedMac = Arrays.copyOfRange(packet.getData(), packet.getLength() - 32, packet.getLength());

                if (verifyHMAC(message, receivedMac)) {
                    String received = new String(message);
                    System.out.println("UDP Server heeft ontvangen van client: " + received);

                    // Verwerk de ontvangen gegevens
                    String[] lines = received.split("\n");
                    if (lines.length >= 6) {
                        String customerName = lines[0];
                        String street = lines[1];
                        String postalCode = lines[2].split(" ")[0];
                        String city = lines[2].substring(postalCode.length()).trim();
                        StringBuilder orderDetailsBuilder = new StringBuilder();
                        for (int i = 3; i < lines.length - 1; i++) {
                            orderDetailsBuilder.append(lines[i]).append("\n");
                        }
                        String orderDetails = orderDetailsBuilder.toString().trim();
                        String orderTime = lines[lines.length - 1];

                        // Sla de bestelling op in de database
                        Server.getInstance().saveOrderToDatabase(customerName, street, city, postalCode, orderDetails, orderTime);
                    } else {
                        System.err.println("Ongeldige bestelling ontvangen.");
                    }

                    // Reageer op de client
                    String response = "Order bevestigd: Dank u!";
                    byte[] responseBytes = response.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, packet.getAddress(), packet.getPort());
                    socket.send(responsePacket);
                    System.out.println("UDP Server heeft bevestiging verzonden.");
                } else {
                    System.err.println("Bericht ontvangen, maar ongeldige MAC.");
                }
            } catch (IOException e) {
                System.err.println("UDP Server fout: " + e.getMessage());
            }
        }
    }

    public void close() {
        socket.close();
        System.out.println("UDP Server verbinding gesloten.");
    }

    private boolean verifyHMAC(byte[] message, byte[] receivedMac) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] calculatedMac = mac.doFinal(message);
            return Arrays.equals(calculatedMac, receivedMac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            System.err.println("Fout bij het verifiëren van HMAC: " + e.getMessage());
            return false;
        }
    }
}
