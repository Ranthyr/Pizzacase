package app.gui;

import javax.swing.*;

import network.tcp.TCPClient;
import network.udp.UDPClient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientGUI {
    private JFrame frame;
    private JPanel panel;
    private JComboBox<String> connectionComboBox;
    private JButton nextButton;
    private JTextField nameField;
    private JTextField addressField;
    private JTextField postalField;
    private JTextField cityField;
    private JComboBox<String> pizzaComboBox;
    private JButton addPizzaButton;
    private JCheckBox[] toppingsCheckBoxes;
    private JButton orderButton;
    private JTextArea cartTextArea;

    private String[] pizzaOptions = {"Margherita", "Pepperoni", "Hawaiian", "Vegetarian", "Seafood", "BBQ Chicken", "Meat Lovers", "Supreme", "Capricciosa", "Quattro Stagioni"};
    private String[] toppingsOptions = {"Mushrooms", "Olives", "Onions", "Peppers", "Tomatoes", "Bacon", "Ham", "Sausage", "Chicken", "Pineapple"};
    private ArrayList<String> cart;
    private Map<String, String> pizzaToppingsMap;

    public void createAndShowGUI() {
        cart = new ArrayList<>();
        pizzaToppingsMap = new HashMap<>();

        // Maak het frame
        frame = new JFrame("Pizza Bestellen - Klant");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);

        // Maak het paneel
        panel = new JPanel();
        frame.add(panel);

        // Voeg GUI-componenten toe
        panel.setLayout(new GridLayout(0, 2, 10, 10));

        JLabel connectionLabel = new JLabel("Stap 1: Kies de verbindingsmethode");
        panel.add(connectionLabel);

        String[] connectionOptions = {"TCP", "UDP"};
        connectionComboBox = new JComboBox<>(connectionOptions);
        panel.add(connectionComboBox);

        nextButton = new JButton("Volgende");
        panel.add(nextButton);

        frame.setVisible(true);

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPersonalInfoScreen();
            }
        });
    }

    private void showPersonalInfoScreen() {
        panel.removeAll();
        panel.setLayout(new GridLayout(0, 2, 10, 10));

        JLabel nameLabel = new JLabel("Naam:");
        panel.add(nameLabel);
        nameField = new JTextField();
        panel.add(nameField);

        JLabel addressLabel = new JLabel("Adres:");
        panel.add(addressLabel);
        addressField = new JTextField();
        panel.add(addressField);

        JLabel postalLabel = new JLabel("Postcode:");
        panel.add(postalLabel);
        postalField = new JTextField();
        panel.add(postalField);

        JLabel cityLabel = new JLabel("Stad:");
        panel.add(cityLabel);
        cityField = new JTextField();
        panel.add(cityField);

        JLabel pizzaLabel = new JLabel("Kies uw pizza:");
        panel.add(pizzaLabel);
        pizzaComboBox = new JComboBox<>(pizzaOptions);
        panel.add(pizzaComboBox);

        JLabel toppingsLabel = new JLabel("Extra toppings:");
        panel.add(toppingsLabel);

        JPanel toppingsPanel = new JPanel(new GridLayout(0, 1));
        toppingsCheckBoxes = new JCheckBox[toppingsOptions.length];
        for (int i = 0; i < toppingsOptions.length; i++) {
            toppingsCheckBoxes[i] = new JCheckBox(toppingsOptions[i]);
            toppingsPanel.add(toppingsCheckBoxes[i]);
        }
        panel.add(toppingsPanel);

        addPizzaButton = new JButton("Voeg toe aan winkelmandje");
        panel.add(addPizzaButton);

        orderButton = new JButton("Bestel");
        panel.add(orderButton);

        cartTextArea = new JTextArea();
        cartTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(cartTextArea);
        panel.add(scrollPane);

        frame.revalidate();
        frame.repaint();

        addPizzaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pizza = (String) pizzaComboBox.getSelectedItem();
                StringBuilder selectedToppings = new StringBuilder();
                for (JCheckBox checkBox : toppingsCheckBoxes) {
                    if (checkBox.isSelected()) {
                        selectedToppings.append(checkBox.getText()).append(", ");
                    }
                }
                String toppings = selectedToppings.toString();
                // Verwijder de laatste komma en spatie als er toppings zijn geselecteerd
                if (!toppings.isEmpty()) {
                    toppings = toppings.substring(0, toppings.length() - 2);
                }

                String pizzaOrder = pizza + (toppings.isEmpty() ? "" : " (" + toppings + ")");
                cart.add(pizzaOrder);
                updateCartTextArea();
            }
        });

        orderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Verwerk de bestelling
                String name = nameField.getText();
                String address = addressField.getText();
                String postal = postalField.getText();
                String city = cityField.getText();
                StringBuilder order = new StringBuilder();
                order.append("Bestelling voor ").append(name).append(":\n");
                order.append("Adres: ").append(address).append(", ").append(postal).append(" ").append(city).append("\n");
                order.append("Bestelling(en):\n");
                for (String pizzaOrder : cart) {
                    order.append("- ").append(pizzaOrder).append("\n");
                }

                // Stuur de bestelling naar de server
                String selectedConnection = (String) connectionComboBox.getSelectedItem();
                if (selectedConnection.equals("TCP")) {
                    TCPClient tcpClient = new TCPClient();
                    tcpClient.connectToServer(order.toString());
                } else {
                    UDPClient udpClient = new UDPClient();
                    udpClient.connectToServer(order.toString());
                }

                // Schakel de bestelknop uit om herhaalde bestellingen te voorkomen
                orderButton.setEnabled(false);

                JOptionPane.showMessageDialog(frame, "Bestelling verzonden naar server:\n" + order.toString());
            }
        });
    }

    private void updateCartTextArea() {
        StringBuilder cartContent = new StringBuilder();
        for (String item : cart) {
            cartContent.append(item).append("\n");
        }
        cartTextArea.setText(cartContent.toString());
    }
}

