package app.gui;

import network.tcp.TCPClient;
import network.udp.UDPClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    private TCPClient tcpClient;
    private UDPClient udpClient;

    private String[] pizzaOptions = {"Margherita", "Pepperoni", "Hawaiian", "Vegetarian", "Seafood", "BBQ Chicken", "Meat Lovers", "Supreme", "Capricciosa", "Quattro Stagioni"};
    private String[] toppingsOptions = {"Mushrooms", "Olives", "Onions", "Peppers", "Tomatoes", "Bacon", "Ham", "Sausage", "Chicken", "Pineapple"};

    public void createAndShowGUI() {
        frame = new JFrame("Pizza Bestellen - Klant");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        panel = new JPanel(new GridLayout(0, 2, 10, 10));
        frame.add(panel);

        JLabel connectionLabel = new JLabel("Stap 1: Kies de verbindingsmethode");
        panel.add(connectionLabel);

        String[] connectionOptions = {"TCP", "UDP"};
        connectionComboBox = new JComboBox<>(connectionOptions);
        panel.add(connectionComboBox);

        nextButton = new JButton("Volgende");
        panel.add(nextButton);

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPersonalInfoScreen();
            }
        });

        frame.setVisible(true);
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
                if (!toppings.isEmpty()) {
                    toppings = toppings.substring(0, toppings.length() - 2);
                }

                String pizzaOrder = pizza + (toppings.isEmpty() ? "" : " (" + toppings + ")");
                cartTextArea.append(pizzaOrder + "\n");
            }
        });

        orderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String address = addressField.getText();
                String postal = postalField.getText();
                String city = cityField.getText();
                String order = "Bestelling voor " + name + ":\n" +
                        "Adres: " + address + ", " + postal + " " + city + "\n" +
                        "Bestelling(en):\n" + cartTextArea.getText();

                String selectedConnection = (String) connectionComboBox.getSelectedItem();
                if (selectedConnection.equals("TCP")) {
                    tcpClient = new TCPClient();
                    tcpClient.connectToServer(order);
                    String response = tcpClient.getResponse();
                    JOptionPane.showMessageDialog(frame, response);
                } else {
                    udpClient = new UDPClient();
                    udpClient.connectToServer(order);
                    String response = udpClient.getResponse();
                    JOptionPane.showMessageDialog(frame, response);
                }

                orderButton.setEnabled(false);
            }
        });

        frame.revalidate();
        frame.repaint();
    }
}
