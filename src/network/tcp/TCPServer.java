package network.tcp;

import app.gui.ServerGUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable {
    private static final int SERVER_PORT = 5000;
    private ServerGUI serverGUI;

    public TCPServer(ServerGUI serverGUI) {
        this.serverGUI = serverGUI;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            serverGUI.addOrder("TCP Server started on port " + SERVER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                            String clientMessage = in.readLine();
                            serverGUI.addOrder(clientMessage);
                            System.out.println("Received from client: " + clientMessage);
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
