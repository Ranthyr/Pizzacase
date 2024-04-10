import app.gui.ClientGUI;
import app.gui.ServerGUI;
import network.tcp.TCPSSLServer;
import network.udp.UDPServer;

public class Main {
    public static void main(String[] args) {
        // Start de TCP-server in een aparte thread
        new Thread(new TCPSSLServer()).start();

        // Stel een gedeeld geheim sleutel in
        String sharedSecretKey = "geheimeSleutel";

        // Maak een nieuwe instantie van UDPServer met de gedeelde geheime sleutel
        UDPServer udpServer = new UDPServer(sharedSecretKey);

        // Start de UDP-server in een nieuwe thread
        new Thread(udpServer).start();

        // Maak en toon de klant GUI in een aparte thread
        new Thread(() -> {
            new ClientGUI();
        }).start();

        // Maak en toon de server GUI in een aparte thread
        new Thread(() -> {
            new ServerGUI();
        }).start();
    }
}
