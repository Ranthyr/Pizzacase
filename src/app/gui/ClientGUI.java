package app.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import network.tcp.TCPSSLClient;
import network.udp.UDPClient;

// GUI class for the client application
public class ClientGUI {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    private GUIComponent connectionPanel;
    private GUIComponent orderPanel;
    private GUIComponent addressPanel;
    private GUIComponent confirmPanel;

    private TCPSSLClient tcpClient;
    private UDPClient udpClient;

    private String orderDetails = "";
    private String addressDetails = "";

    // Constructor to initialize the GUI
    public ClientGUI() {
        initializeGUI();
    }

    // Initialize the GUI components
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

        cardPanel.add((JPanel) connectionPanel.getComponent(), "ConnectionPanel");
        cardPanel.add((JPanel) orderPanel.getComponent(), "OrderPanel");
        cardPanel.add((JPanel) addressPanel.getComponent(), "AddressPanel");
        cardPanel.add((JPanel) confirmPanel.getComponent(), "ConfirmPanel");

        frame.add(cardPanel);
        cardLayout.show(cardPanel, "ConnectionPanel");

        frame.setVisible(true);
    }

    // Start TCP connection
    public void startTCPConnection() {
        tcpClient = new TCPSSLClient();
        tcpClient.connectToServer("localhost", 5000);
    }

    // Start UDP connection
    public void startUDPConnection() {
        udpClient = new UDPClient();
        String sharedSecretKey = "geheimeSleutel"; 
        udpClient.connectToServer("localhost", 5001, sharedSecretKey);
    }

    // Send order details to the server
    public void sendOrder(String order) {
        orderDetails = order;
        if (!addressDetails.isEmpty()) {
            String completeOrder = addressDetails + "\n" + orderDetails;
            sendMessageToServer(completeOrder);
            navigateToConfirmPanel();
        } else {
            navigateToAddressPanel();
        }
    }

    // Send address details to the server
    public void sendAddress(String name, String street, String city, String postalCode) {
        addressDetails = String.format("%s\n%s\n%s\n%s", name, street, city, postalCode);
        if (!orderDetails.isEmpty()) {
            String completeOrder = addressDetails + "\n" + orderDetails;
            sendMessageToServer(completeOrder);
            navigateToConfirmPanel();
        }
    }

    // Navigate to the next panel
    public void navigateToNextPanel() {
        cardLayout.next(cardPanel);
    }

    // Navigate to a specific panel
    public void navigateToPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
    }

    // Send message to the server (either TCP or UDP)
    private void sendMessageToServer(String message) {
        if (tcpClient != null) {
            System.out.println("[TCP Client] Bericht verzenden naar server: " + message);
            tcpClient.sendMessage(message);
        } else if (udpClient != null) {
            System.out.println("[UDP Client] Bericht verzenden naar server: " + message);
            udpClient.sendMessage(message);
        } else {
            System.err.println("Geen actieve verbinding met de server.");
        }
    }

    // Navigate to the address panel
    private void navigateToAddressPanel() {
        System.out.println("Navigeren naar Adresgegevens panel...");
        navigateToPanel("AddressPanel");
    }

    // Navigate to the confirmation panel
    private void navigateToConfirmPanel() {
        ((ConfirmPanel)confirmPanel).setOrderDetails(orderDetails);
        ((ConfirmPanel)confirmPanel).setAddressDetails(addressDetails);
        navigateToPanel("ConfirmPanel");
    }

    // Interface for GUI components
    interface GUIComponent {
        Component getComponent();
    }

    // Connection panel class
    class ConnectionPanel extends JPanel implements GUIComponent {
        private JComboBox<String> connectionTypeComboBox;
        private JButton connectButton;

        // Constructor for the connection panel
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
                gui.navigateToNextPanel();
            });
            add(connectButton);
        }

        // Get the component of the panel
        public Component getComponent() {
            return this;
        }
    }

    // Order panel class
    class OrderPanel extends JPanel implements GUIComponent {
        private DefaultTableModel tableModel;
        private JTable orderTable;
        private JComboBox<String> pizzaComboBox;
        private Map<String, JCheckBox> toppingsCheckBoxes = new HashMap<>();
        private JButton addPizzaButton;
        private JButton removePizzaButton;
        private JButton nextButton;

        // Constructor for the order panel
        public OrderPanel(ClientGUI gui) {
            setLayout(new BorderLayout());

            // Initialize pizza combo box
            String[] pizzas = {"Calzone", "Diavolo", "Mozzarella"};
            pizzaComboBox = new JComboBox<>(pizzas);

            // Initialize toppings panel
            JPanel toppingsPanel = new JPanel();
            toppingsPanel.setLayout(new BoxLayout(toppingsPanel, BoxLayout.Y_AXIS));
            String[] toppings = {"Extra Cheese", "Mushrooms", "Olives", "Peppers"};
            for (String topping : toppings) {
                JCheckBox checkBox = new JCheckBox(topping);
                toppingsCheckBoxes.put(topping, checkBox);
                toppingsPanel.add(checkBox);
            }

            // Initialize table for order details
            String[] columnNames = {"Pizza", "Quantity", "Toppings"};
            tableModel = new DefaultTableModel(columnNames, 0);
            orderTable = new JTable(tableModel) {
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            // Initialize buttons
            addPizzaButton = new JButton("Add Pizza");
            addPizzaButton.addActionListener(e -> addPizza());

            removePizzaButton = new JButton("Remove Pizza");
            removePizzaButton.addActionListener(e -> removePizza());

            nextButton = new JButton("Next");
            nextButton.addActionListener(e -> {
                String order = compileOrder();
                if (!order.isEmpty()) {
                    gui.sendOrder(order);
                } else {
                    JOptionPane.showMessageDialog(this, "Selecteer minstens één pizza.", "Waarschuwing", JOptionPane.WARNING_MESSAGE);
                }
            });

            // Initialize button panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(addPizzaButton);
            buttonPanel.add(removePizzaButton);
            buttonPanel.add(nextButton);

            // Add components to the order panel
            add(pizzaComboBox, BorderLayout.NORTH);
            add(new JScrollPane(orderTable), BorderLayout.CENTER);
            add(toppingsPanel, BorderLayout.WEST);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        // Add pizza to the order
        private void addPizza() {
            String selectedPizza = (String) pizzaComboBox.getSelectedItem();
            int quantity = 1;
            StringBuilder toppingsBuilder = new StringBuilder();

            toppingsCheckBoxes.entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .forEach(entry -> {
                    toppingsBuilder.append(entry.getKey()).append(", ");
                    entry.getValue().setSelected(false);
                });

            String toppings = toppingsBuilder.toString();
            if (!toppings.isEmpty()) {
                toppings = toppings.substring(0, toppings.length() - 2);
            }

            tableModel.addRow(new Object[]{selectedPizza, quantity, toppings});
        }

        // Remove pizza from the order
        private void removePizza() {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Selecteer een rij om te verwijderen.", "Waarschuwing", JOptionPane.WARNING_MESSAGE);
            }
        }

        // Compile order details
        private String compileOrder() {
            StringBuilder order = new StringBuilder();
            boolean hasOrder = false;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                order.append(tableModel.getValueAt(i, 0)).append("\n");
                order.append(tableModel.getValueAt(i, 1)).append("\n");
                if (!tableModel.getValueAt(i, 2).equals("")) {
                    Arrays.stream(((String) tableModel.getValueAt(i, 2)).split(", ")).forEach(topping -> order.append(topping).append("\n"));
                    hasOrder = true;
                }
                order.append("\n");
            }
            if (hasOrder) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                order.append(dateFormat.format(new Date()));
            }
            return order.toString();
        }

        // Get the component of the panel
        public Component getComponent() {
            return this;
        }
    }

    // Address panel class
    class AddressPanel extends JPanel implements GUIComponent {
        private final JTextField nameField;
        private final JTextField addressField;
        private final JTextField cityField;
        private final JTextField postalCodeField;
    
        // Constructor for the address panel
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
                String name = nameField.getText().trim();
                String street = addressField.getText().trim();
                String city = cityField.getText().trim();
                String postalCode = postalCodeField.getText().trim();
    
                if (validateInput(name, street, city, postalCode)) {
                    gui.sendAddress(name, street, city, postalCode);
                } else {
                    JOptionPane.showMessageDialog(this, "Ongeldige invoer. Controleer uw gegevens en probeer opnieuw.", "Invoerfout", JOptionPane.ERROR_MESSAGE);
                }
            });
            add(nextButton);
        }
    
        // Validate address input
        private boolean validateInput(String name, String street, String city, String postalCode) {
            if (!name.matches(".*\\p{L}.*")) {
                return false;
            }
    
            if (!street.matches(".*\\p{L}.*") || !street.matches(".*\\d.*")) {
                return false;
            }
    
            if (!city.matches(".*\\p{L}.*")) {
                return false;
            }

            String regexPostalCode = "^\\d{4}\\s?[a-zA-Z]{2}$";
            if (!Pattern.matches(regexPostalCode, postalCode)) {
                return false;
            }
    
            return true;
        }
    
        // Get the component of the panel
        public Component getComponent() {
            return this;
        }
    }

    // Confirmation panel class
    class ConfirmPanel extends JPanel implements GUIComponent {
        private final JTextArea confirmTextArea;
        public ConfirmPanel(ClientGUI gui) {
            setLayout(new BorderLayout());
            confirmTextArea = new JTextArea();
            confirmTextArea.setEditable(false);
            add(new JScrollPane(confirmTextArea), BorderLayout.CENTER);

            JButton closeButton = new JButton("Sluit");
            closeButton.addActionListener(e -> gui.navigateToPanel("ConnectionPanel"));
            add(closeButton, BorderLayout.SOUTH);
        }

        // Set order details in the confirmation panel
        public void setOrderDetails(String order) {
            confirmTextArea.setText(order);
        }

        // Set address details in the confirmation panel
        public void setAddressDetails(String address) {
            confirmTextArea.append("\n\nAdresgegevens:\n" + address);
        }

        // Get the component of the panel
        public Component getComponent() {
            return this;
        }
    }

    // Main method to start the client GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI());
    }
}
