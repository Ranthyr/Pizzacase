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
import java.util.regex.Pattern;

public class UDPServer implements Runnable {
    public final static int PORT = 5001;
    private static final int BUFFER_SIZE = 1024;
    private final SecretKeySpec secretKey;
    private final Pattern postalCodePattern = Pattern.compile("\\d{4}[\\s]?[a-zA-Z]{2}");

    private DatagramSocket socket;
    private byte[] buffer = new byte[BUFFER_SIZE];

    // Constructor to initialize the UDPServer
    public UDPServer(String sharedSecretKey) {
        this.secretKey = new SecretKeySpec(sharedSecretKey.getBytes(), "HmacSHA256");

        try {
            socket = new DatagramSocket(PORT);
            System.out.println("UDP Server started on port " + PORT);
        } catch (SocketException e) {
            System.err.println("UDP Server could not start on port " + PORT + ": " + e.getMessage());
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
                    System.out.println("UDP Server received from client: " + received);

                    // Process the received data
                    String[] lines = received.split("\n");
                    if (lines.length >= 6) {
                        String customerName = lines[0];
                        String street = lines[1];
                        String postalCode = lines[2].split(" ")[0];
                        String city = lines[2].substring(postalCode.length()).trim();

                        if (!postalCodePattern.matcher(postalCode).matches()) {
                            System.err.println("Invalid postal code received: " + postalCode);
                            continue; // Continue with the next messages
                        }

                        StringBuilder orderDetailsBuilder = new StringBuilder();
                        for (int i = 3; i < lines.length - 1; i++) {
                            orderDetailsBuilder.append(lines[i]).append("\n");
                        }
                        String orderDetails = orderDetailsBuilder.toString().trim();
                        String orderTime = lines[lines.length - 1];

                        // Save the order to the database
                        Server.getInstance().saveOrderToDatabase(customerName, street, city, postalCode, orderDetails, orderTime);
                    } else {
                        System.err.println("Invalid order received.");
                    }

                    // Respond to the client
                    String response = "Order confirmed: Thank you!";
                    byte[] responseBytes = response.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, packet.getAddress(), packet.getPort());
                    socket.send(responsePacket);
                    System.out.println("UDP Server sent confirmation.");
                } else {
                    System.err.println("Message received, but invalid MAC.");
                }
            } catch (IOException e) {
                System.err.println("UDP Server error: " + e.getMessage());
            }
        }
    }

    // Method to close the connection
    public void close() {
        socket.close();
        System.out.println("UDP Server connection closed.");
    }

    // Method to verify HMAC for message integrity
    private boolean verifyHMAC(byte[] message, byte[] receivedMac) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] calculatedMac = mac.doFinal(message);
            return Arrays.equals(calculatedMac, receivedMac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            System.err.println("Error verifying HMAC: " + e.getMessage());
            return false;
        }
    }
}
