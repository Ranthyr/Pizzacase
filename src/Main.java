import app.gui.ClientGUI;
import app.gui.ServerGUI;
import network.tcp.TCPServer;
import network.udp.UDPServer;

public class Main {
    public static void main(String[] args) {
        // Start de TCP-server in een aparte thread
        new Thread(new TCPServer()).start();

        // Start de UDP-server in een aparte thread
        new Thread(new UDPServer()).start();

        // Maak en toon de klant GUI in een aparte thread
        new Thread(() -> {
            ClientGUI clientGUI = new ClientGUI();
        }).start();

        // Maak en toon de server GUI in een aparte thread
        new Thread(() -> {
            ServerGUI serverGUI = new ServerGUI();
        }).start();
    }
}
