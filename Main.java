import app.client.Client;
import app.gui.ClientGUI;
import app.gui.ServerGUI;
import app.server.Server;

public class Main {
        public static void main(String[] args) {
        // Start de server
        new Thread(new Server()).start();

        // Start de client
        new Thread(new Client()).start();

        // Start de server GUI
        new ServerGUI().start();

        // Start de client GUI
        new ClientGUI().start();
    }
}