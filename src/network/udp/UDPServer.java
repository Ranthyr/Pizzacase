package network.udp;

import app.gui.ServerGUI;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer implements Runnable {
    private static final int SERVER_PORT = 5001;
    private ServerGUI serverGUI;

    public UDPServer(ServerGUI serverGUI) {
        this.serverGUI = serverGUI;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(SERVER_PORT)) {
            serverGUI.addOrder("UDP Server started on port " + SERVER_PORT);

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String clientMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                serverGUI.addOrder("Received from client: " + clientMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
