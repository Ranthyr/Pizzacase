package network.udp;

import java.io.*;
import java.net.*;

public class UDPServer implements Runnable {
    private static final int PORT = 5001;

    @Override
    public void run() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            System.out.println("UDP Server started on port " + PORT);

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                System.out.println("Received from client " + clientAddress + ":" + clientPort + ": " + message);

                // Process client's message

                // Send response back to client
                String responseMessage = "Order received. Thank you!";
                byte[] sendData = responseMessage.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
