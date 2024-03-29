package app.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ServerGUI {
    private JFrame frame;
    private JPanel panel;
    private JTextArea ordersTextArea;

    private ArrayList<String> ordersList;

    public ServerGUI() {
        ordersList = new ArrayList<>();

        // Maak het frame
        frame = new JFrame("Pizza Bestellen - Restaurant");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        // Maak het paneel
        panel = new JPanel();
        frame.add(panel);

        // Voeg GUI-componenten toe
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Bestellingen overzicht");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        ordersTextArea = new JTextArea();
        ordersTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(ordersTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Toon het frame
        frame.setVisible(true);
    }

    public void addOrder(String order) {
        ordersList.add(order);
        updateOrdersTextArea();
    }

    private void updateOrdersTextArea() {
        StringBuilder orders = new StringBuilder();
        for (String order : ordersList) {
            orders.append(order).append("\n\n");
        }
        ordersTextArea.setText(orders.toString());
    }
}

