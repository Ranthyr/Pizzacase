package app.gui;

import app.server.Server;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ServerGUI {
    private JFrame frame;
    private JTextArea orderTextArea;

    public ServerGUI() {
        frame = new JFrame("Server Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        orderTextArea = new JTextArea(20, 40);
        orderTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(orderTextArea);

        panel.add(scrollPane, BorderLayout.CENTER);

        frame.getContentPane().add(panel);
        frame.pack();
    }

    public void show() {
        frame.setVisible(true);
    }

    public void updateOrders() {
        // Haal bestellingen op uit de database en update de tekstgebied
        List<String> orders = Server.getInstance().getOrders();
        StringBuilder sb = new StringBuilder();
        for (String order : orders) {
            sb.append(order).append("\n");
        }
        orderTextArea.setText(sb.toString());
    }
}