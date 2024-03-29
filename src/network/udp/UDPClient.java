package network.udp;

import java.io.*;
import java.net.*;

public class UDPClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5001;

    public void connectToServer(String order) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);

            // Send order to server
            byte[] sendData = order.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            clientSocket.send(sendPacket);

            // Close connection
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
