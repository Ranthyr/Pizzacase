package app.gui;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import network.tcp.TCPClient;
import network.udp.UDPClient;
import network.udp.UDPServer;
import java.util.Date;


public class ClientGUI {
    // Hoofdcomponenten
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Paneelklassen
    private ConnectionPanel connectionPanel;
    private OrderPanel orderPanel;
    private AddressPanel addressPanel;
    private ConfirmPanel confirmPanel;

    // Netwerk clients
    private TCPClient tcpClient;
    private UDPClient udpClient;

    // Bestel- en adresgegevens
    private String orderDetails = "";
    private String addressDetails = "";

    // Constructor
    public ClientGUI() {
        initializeGUI();
    }

    // Hoofd initialisatie methode
    public void initializeGUI() {
        frame = new JFrame("Pizza Bestel Systeem");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        connectionPanel = new ConnectionPanel(this);
        orderPanel = new OrderPanel(this);
        addressPanel = new AddressPanel(this);
        confirmPanel = new ConfirmPanel(this);

        cardPanel.add(connectionPanel, "ConnectionPanel");
        cardPanel.add(orderPanel, "OrderPanel");
        cardPanel.add(addressPanel, "AddressPanel");
        cardPanel.add(confirmPanel, "ConfirmPanel");

        frame.add(cardPanel);
        cardLayout.show(cardPanel, "ConnectionPanel");

        frame.setVisible(true);
    }

    // Methode om TCP-verbinding te starten
    public void startTCPConnection() {
        tcpClient = new TCPClient();
        tcpClient.connectToServer("localhost", 5000);
        cardLayout.show(cardPanel, "OrderPanel");
    }

    // Methode om UDP-verbinding te starten
    public void startUDPConnection() {
        udpClient = new UDPClient();
        udpClient.connectToServer("localhost", UDPServer.PORT);
        cardLayout.show(cardPanel, "OrderPanel");
    }

    // Methode om de bestelling te versturen en naar het adrespaneel te navigeren
public void sendOrder(String order) {
    // Voeg datum en tijd toe aan de bestelling
    String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
    orderDetails = order.trim() + "\n" + timeStamp; // Trim om overtollige lege regels te verwijderen

    cardLayout.show(cardPanel, "AddressPanel");
}

public void sendAddress(String address) {
    addressDetails = address;
    String finalOrder = addressDetails + "\n" + orderDetails;

    // Verstuur de bestelling naar de server
    if (tcpClient != null) {
        tcpClient.sendMessage(finalOrder);
    } else if (udpClient != null) {
        udpClient.sendMessage(finalOrder);
    }

    // Ga naar het bevestigingspaneel
    confirmPanel.setOrderDetails(finalOrder);
    cardLayout.show(cardPanel, "ConfirmPanel");
}

    // Methode om de applicatie te starten
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI());
    }

    class ConnectionPanel extends JPanel {
        private JComboBox<String> connectionTypeComboBox;
        private JButton connectButton;

        public ConnectionPanel(ClientGUI gui) {
            setLayout(new GridLayout(3, 1, 10, 10));
            add(new JLabel("Selecteer het type verbinding:"));

            connectionTypeComboBox = new JComboBox<>(new String[] { "TCP", "UDP" });
            add(connectionTypeComboBox);

            connectButton = new JButton("Verbinden");
            connectButton.addActionListener(e -> {
                String connectionType = (String) connectionTypeComboBox.getSelectedItem();
                if ("TCP".equals(connectionType)) {
                    gui.startTCPConnection();
                } else {
                    gui.startUDPConnection();
                }
            });
            add(connectButton);
        }
    }

    class OrderPanel extends JPanel {
        private final ClientGUI gui;
        private final JTextArea orderTextArea;
        private final Map<String, JCheckBox> toppingsCheckBoxes;
        private final ButtonGroup pizzaButtonGroup; // ButtonGroup voor de pizza selectie

        public OrderPanel(ClientGUI gui) {
            this.gui = gui;
            this.setLayout(new BorderLayout(10, 10));
            this.toppingsCheckBoxes = new HashMap<>();
            this.pizzaButtonGroup = new ButtonGroup(); // Initialiseer de ButtonGroup

            // Pizza selectie paneel
            JPanel pizzaSelectionPanel = new JPanel();
            pizzaSelectionPanel.setLayout(new BoxLayout(pizzaSelectionPanel, BoxLayout.Y_AXIS));
            pizzaSelectionPanel.setBorder(BorderFactory.createTitledBorder("Kies uw pizza:"));

            String[] pizzas = { "Margherita", "Pepperoni", "Hawaiian" };
            for (String pizza : pizzas) {
                JRadioButton pizzaButton = new JRadioButton(pizza);
                pizzaButton.setActionCommand(pizza); // Gebruik de pizzanaam als actiecommando
                pizzaButtonGroup.add(pizzaButton); // Voeg de button toe aan de groep
                pizzaSelectionPanel.add(pizzaButton);
            }

            // Toppings selectie paneel
            JPanel toppingsPanel = new JPanel();
            toppingsPanel.setLayout(new BoxLayout(toppingsPanel, BoxLayout.Y_AXIS));
            toppingsPanel.setBorder(BorderFactory.createTitledBorder("Extra toppings:"));

            String[] toppings = { "Mushrooms", "Olives", "Peppers" };
            for (String topping : toppings) {
                JCheckBox checkBox = new JCheckBox(topping);
                toppingsPanel.add(checkBox);
                toppingsCheckBoxes.put(topping, checkBox);
            }

            // Order TextArea
            orderTextArea = new JTextArea(5, 20);
            orderTextArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(orderTextArea);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Winkelmand"));

            // Button panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            JButton addToOrderButton = new JButton("Voeg toe aan bestelling");
            addToOrderButton.addActionListener(e -> updateOrder());

            JButton nextButton = new JButton("Ga naar adresgegevens");
            nextButton.addActionListener(e -> gui.sendOrder(orderTextArea.getText()));

            buttonPanel.add(addToOrderButton);
            buttonPanel.add(nextButton);

            // Add components to OrderPanel
            this.add(pizzaSelectionPanel, BorderLayout.WEST);
            this.add(toppingsPanel, BorderLayout.CENTER);
            this.add(scrollPane, BorderLayout.EAST);
            this.add(buttonPanel, BorderLayout.SOUTH);
        }

        private void updateOrder() {
            // Vind de geselecteerde pizza en voeg deze toe aan de bestelling met het aantal
            Enumeration<AbstractButton> pizzaButtons = pizzaButtonGroup.getElements();
            while (pizzaButtons.hasMoreElements()) {
                JRadioButton pizzaButton = (JRadioButton) pizzaButtons.nextElement();
                if (pizzaButton.isSelected()) {
                    orderTextArea.append(pizzaButton.getText() + "\n1\n"); // Voeg 1 toe voor het aantal pizza's
                }
            }

            // Voeg het aantal geselecteerde toppings toe
            int toppingsCount = 0;
            StringBuilder toppingsBuilder = new StringBuilder();
            for (Map.Entry<String, JCheckBox> toppingEntry : toppingsCheckBoxes.entrySet()) {
                if (toppingEntry.getValue().isSelected()) {
                    toppingsBuilder.append(toppingEntry.getKey()).append("\n");
                    toppingsCount++;
                    toppingEntry.getValue().setSelected(false);
                }
            }

            if (toppingsCount > 0) {
                orderTextArea.append(toppingsCount + "\n" + toppingsBuilder.toString());
            } else {
                orderTextArea.append("0\n"); // Als er geen toppings zijn geselecteerd
            }
            orderTextArea.append("\n"); // Voeg een lege regel toe na elke bestelling
        }
    }

    class AddressPanel extends JPanel {
        private final JTextField nameField;
        private final JTextField addressField;
        private final JTextField cityField;
        private final JTextField postalCodeField;

        public AddressPanel(ClientGUI gui) {
            setLayout(new GridLayout(5, 2, 10, 10));

            add(new JLabel("Naam:"));
            nameField = new JTextField();
            add(nameField);

            add(new JLabel("Adres:"));
            addressField = new JTextField();
            add(addressField);

            add(new JLabel("Stad:"));
            cityField = new JTextField();
            add(cityField);

            add(new JLabel("Postcode:"));
            postalCodeField = new JTextField();
            add(postalCodeField);

            JButton nextButton = new JButton("Volgende");
            nextButton.addActionListener(e -> {
                // Gebruik direct de tekstvelden zonder labels voor de format
                String address = String.format("%s\n%s\n%s\n%s",
                        nameField.getText(),
                        addressField.getText(),
                        cityField.getText(),
                        postalCodeField.getText());
                gui.sendAddress(address);
            });
            add(nextButton);
        }
    }

    class ConfirmPanel extends JPanel {
        private final JTextArea confirmTextArea;

        public ConfirmPanel(ClientGUI gui) {
            setLayout(new BorderLayout());
            confirmTextArea = new JTextArea();
            confirmTextArea.setEditable(false);
            add(new JScrollPane(confirmTextArea), BorderLayout.CENTER);

            JButton closeButton = new JButton("Sluit");
            closeButton.addActionListener(e -> System.exit(0));
            add(closeButton, BorderLayout.SOUTH);
        }

        public void setOrderDetails(String order) {
            confirmTextArea.setText(order);
        }

        public void setAddressDetails(String address) {
            confirmTextArea.append("\n\nAdresgegevens:\n" + address);
        }
    }
}