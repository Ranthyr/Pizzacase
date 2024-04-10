package app.gui;

import app.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ServerGUI {
    private JFrame frame;
    private JTextArea orderListTextArea;

    public ServerGUI() {
        initializeGUI();
        fetchOrdersFromDatabase();
    }

    private void initializeGUI() {
        frame = new JFrame("Server - Bestellingen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        orderListTextArea = new JTextArea();
        orderListTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(orderListTextArea);

        // Voeg de JTextArea en de Vernieuwen knop toe aan het frame
        frame.add(scrollPane, BorderLayout.CENTER);
        JButton refreshButton = new JButton("Vernieuwen");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchOrdersFromDatabase();
            }
        });
        frame.add(refreshButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void fetchOrdersFromDatabase() {
        // Ophalen van bestellingen uit de database en weergeven in het JTextArea
        Server server = Server.getInstance();
        try {
            Statement statement = server.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM orders");

            StringBuilder orders = new StringBuilder();
            while (resultSet.next()) {
                String customerName = resultSet.getString("customer_name");
                int pizzaCount = resultSet.getInt("pizza_count");
                String orderDetails = resultSet.getString("order_details");
                orders.append("Klant: ").append(customerName).append("\nAantal pizza's: ").append(pizzaCount).append("\nBestelling: ").append(orderDetails).append("\n\n");
            }

            orderListTextArea.setText(orders.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Fout bij het ophalen van bestellingen uit de database: " + e.getMessage(), "Databasefout", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}
