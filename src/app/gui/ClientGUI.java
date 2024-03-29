package app.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import network.tcp.TCPClient;
import network.udp.UDPClient;
import network.udp.UDPServer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ClientGUI {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel connectionPanel;
    private JPanel orderPanel;
    private JPanel addressPanel;
    private JPanel confirmPanel;
    private JComboBox<String> connectionComboBox;
    private JButton connectButton;
    private JButton nextToConfirmButton;
    private JPanel toppingsPanel;
    private Map<String, JCheckBox> toppingsCheckBoxes;
    private JProgressBar progressBar;

    private TCPClient tcpClient;
    private UDPClient udpClient;

    private final String[] pizzaOptions = {"Margherita", "Pepperoni", "Hawaiian", "Vegetarian", "Seafood", "BBQ Chicken", "Meat Lovers", "Supreme", "Capricciosa", "Quattro Stagioni"};
    private final String[] toppingsOptions = {"Mushrooms", "Olives", "Onions", "Peppers", "Tomatoes", "Bacon", "Ham", "Sausage", "Chicken", "Pineapple"};

    public void createAndShowGUI() {
        frame = new JFrame("Pizza Bestellen - Klant");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        frame.add(mainPanel);

        connectionPanel = createConnectionPanel();
        mainPanel.add(connectionPanel, "Connection");

        orderPanel = createOrderPanel();
        mainPanel.add(orderPanel, "Order");

        addressPanel = createAddressPanel();
        mainPanel.add(addressPanel, "Address");

        confirmPanel = createConfirmPanel();
        mainPanel.add(confirmPanel, "Confirm");

        cardLayout.show(mainPanel, "Connection");

        frame.setVisible(true);
    }

    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel centerPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.add(centerPanel, BorderLayout.CENTER);

        JLabel connectionLabel = new JLabel("Kies de verbindingsmethode:");
        centerPanel.add(connectionLabel);

        String[] connectionOptions = {"TCP", "UDP"};
        connectionComboBox = new JComboBox<>(connectionOptions);
        centerPanel.add(connectionComboBox);

        connectButton = new JButton("Verbinden");
        centerPanel.add(connectButton);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedConnection = (String) connectionComboBox.getSelectedItem();
                if (selectedConnection.equals("TCP")) {
                    tcpClient = new TCPClient();
                    tcpClient.connectToServer("localhost", 5000); // Pass appropriate host and port
                    JOptionPane.showMessageDialog(frame, "TCP connection established!");
                } else {
                    udpClient = new UDPClient();
                    udpClient.connectToServer("localhost", UDPServer.PORT); // Pass appropriate host and port
                    JOptionPane.showMessageDialog(frame, "UDP connection established!");
                }
                cardLayout.show(mainPanel, "Order");
            }
        });

        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel pizzaPanel = createPizzaPanel();
        topPanel.add(pizzaPanel);

        toppingsPanel = createToppingsPanel();
        topPanel.add(toppingsPanel);

        JTextArea cartTextArea = new JTextArea(10, 20);
        cartTextArea.setEditable(false);
        JScrollPane cartScrollPane = new JScrollPane(cartTextArea);
        panel.add(cartScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(buttonPanel, BorderLayout.SOUTH);

        JButton addToCartButton = new JButton("Voeg toe aan winkelmandje");
        buttonPanel.add(addToCartButton);

        JButton modifyCartButton = new JButton("Winkelmandje wijzigen");
        buttonPanel.add(modifyCartButton);

        JButton continueButton = new JButton("Doorgaan");
        buttonPanel.add(continueButton);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        panel.add(progressBar, BorderLayout.SOUTH);

        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder orderBuilder = new StringBuilder();
                for (Component component : pizzaPanel.getComponents()) {
                    if (component instanceof JRadioButton) {
                        JRadioButton radioButton = (JRadioButton) component;
                        if (radioButton.isSelected()) {
                            orderBuilder.append(radioButton.getText()).append("\n");
                        }
                    }
                }
                for (String topping : toppingsCheckBoxes.keySet()) {
                    JCheckBox checkBox = toppingsCheckBoxes.get(topping);
                    if (checkBox.isSelected()) {
                        orderBuilder.append(checkBox.getText()).append("\n");
                        checkBox.setSelected(false); // Clear the checkbox after adding to cart
                    }
                }
                cartTextArea.append(orderBuilder.toString());
            }
        });

        modifyCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implementeer hier de logica om het winkelmandje te wijzigen
                String modifiedCart = JOptionPane.showInputDialog(frame, "Winkelmandje wijzigen", cartTextArea.getText());
                cartTextArea.setText(modifiedCart);
            }
        });

        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Address");
                progressBar.setValue(33); // Update progress bar to 33% completed
            }
        });

        return panel;
    }

    private JPanel createPizzaPanel() {
        JPanel pizzaPanel = new JPanel();
        pizzaPanel.setBorder(BorderFactory.createTitledBorder("Kies uw pizza:"));
        pizzaPanel.setLayout(new BoxLayout(pizzaPanel, BoxLayout.Y_AXIS));
        ButtonGroup pizzaGroup = new ButtonGroup();
        for (String pizzaOption : pizzaOptions) {
            JRadioButton pizzaRadioButton = new JRadioButton(pizzaOption);
            pizzaPanel.add(pizzaRadioButton);
            pizzaGroup.add(pizzaRadioButton);
        }
        return pizzaPanel;
    }

    private JPanel createToppingsPanel() {
        JPanel toppingsPanel = new JPanel();
        toppingsPanel.setBorder(BorderFactory.createTitledBorder("Extra toppings:"));
        toppingsPanel.setLayout(new BoxLayout(toppingsPanel, BoxLayout.Y_AXIS));
        toppingsCheckBoxes = new HashMap<>();
        for (String toppingOption : toppingsOptions) {
            JCheckBox toppingCheckBox = new JCheckBox(toppingOption);
            toppingsPanel.add(toppingCheckBox);
            toppingsCheckBoxes.put(toppingOption, toppingCheckBox);
        }
        return toppingsPanel;
    }

    private JPanel createAddressPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel centerPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(centerPanel, BorderLayout.CENTER);

        JLabel nameLabel = new JLabel("Naam:");
        centerPanel.add(nameLabel);
        JTextField nameField = new JTextField();
        centerPanel.add(nameField);

        JLabel addressLabel = new JLabel("Adres:");
        centerPanel.add(addressLabel);
        JTextField addressField = new JTextField();
        centerPanel.add(addressField);

        JLabel postalLabel = new JLabel("Postcode:");
        centerPanel.add(postalLabel);
        JTextField postalField = new JTextField();
        centerPanel.add(postalField);

        JLabel cityLabel = new JLabel("Stad:");
        centerPanel.add(cityLabel);
        JTextField cityField = new JTextField();
        centerPanel.add(cityField);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        JButton backButton = new JButton("Terug");
        buttonsPanel.add(backButton);

        nextToConfirmButton = new JButton("Volgende");
        buttonsPanel.add(nextToConfirmButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Order");
            }
        });

        nextToConfirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Confirm");
                progressBar.setValue(66); // Update progress bar to 66% completed
            }
        });

        return panel;
    }

    private JPanel createConfirmPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextArea confirmTextArea = new JTextArea(10, 30);
        confirmTextArea.setEditable(false);
        JScrollPane confirmScrollPane = new JScrollPane(confirmTextArea);
        panel.add(confirmScrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Terug naar bestellen");
        panel.add(backButton, BorderLayout.SOUTH);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Order");
            }
        });

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGUI().createAndShowGUI();
            }
        });
    }
}
