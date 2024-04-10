package app.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import network.tcp.TCPClient;
import network.udp.UDPClient;

public class ClientGUI {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    private ConnectionPanel connectionPanel;
    private OrderPanel orderPanel;
    private AddressPanel addressPanel;
    private ConfirmPanel confirmPanel;

    private TCPClient tcpClient;
    private UDPClient udpClient;

    private String orderDetails = "";
    private String addressDetails = "";

    private boolean orderSent = false; // Flag om te controleren of de bestelling al is verzonden

    public ClientGUI() {
        initializeGUI();
    }

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

    public void startTCPConnection() {
        tcpClient = new TCPClient();
        tcpClient.connectToServer("localhost", 5000);
    }

    public void startUDPConnection() {
        udpClient = new UDPClient();
        udpClient.connectToServer("localhost", 5001);
    }

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
    
    public void sendAddress(String address) {
        addressDetails = address;
        if (!orderDetails.isEmpty()) {
            String completeOrder = addressDetails + "\n" + orderDetails;
            sendMessageToServer(completeOrder);
            navigateToConfirmPanel();
        }
    }

    public void navigateToNextPanel() {
        cardLayout.next(cardPanel);
    }

    public void navigateToPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
    }

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

    private void navigateToAddressPanel() {
        System.out.println("Navigeren naar Adresgegevens panel...");
        navigateToNextPanel();
    }

    private void navigateToConfirmPanel() {
        confirmPanel.setOrderDetails(orderDetails);
        confirmPanel.setAddressDetails(addressDetails);
        navigateToPanel("ConfirmPanel");
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
                gui.navigateToNextPanel();
            });
            add(connectButton);
        }
    }

    class OrderPanel extends JPanel {
        private DefaultTableModel tableModel;
        private JTable orderTable;
        private JComboBox<String> pizzaComboBox;
        private Map<String, JCheckBox> toppingsCheckBoxes = new HashMap<>();
        private JButton addPizzaButton;
        private JButton removePizzaButton;
        private JButton nextButton;

        public OrderPanel(ClientGUI gui) {
            setLayout(new BorderLayout());

            // Pizza selector
            String[] pizzas = {"Calzone", "Diavolo", "Mozzarella"}; // Voorbeeldpizza's
            pizzaComboBox = new JComboBox<>(pizzas);

            // Toppings checkboxes
            JPanel toppingsPanel = new JPanel();
            toppingsPanel.setLayout(new BoxLayout(toppingsPanel, BoxLayout.Y_AXIS));
            String[] toppings = {"Extra Cheese", "Mushrooms", "Olives", "Peppers"}; // Voorbeeldtoppings
            for (String topping : toppings) {
                JCheckBox checkBox = new JCheckBox(topping);
                toppingsCheckBoxes.put(topping, checkBox);
                toppingsPanel.add(checkBox);
            }

            // Order table setup
            String[] columnNames = {"Pizza", "Quantity", "Toppings"};
            tableModel = new DefaultTableModel(columnNames, 0);
            orderTable = new JTable(tableModel) {
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

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

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(addPizzaButton);
            buttonPanel.add(removePizzaButton);
            buttonPanel.add(nextButton);

            add(pizzaComboBox, BorderLayout.NORTH);
            add(new JScrollPane(orderTable), BorderLayout.CENTER);
            add(toppingsPanel, BorderLayout.WEST);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void addPizza() {
            String selectedPizza = (String) pizzaComboBox.getSelectedItem();
            int quantity = 1; // Standaardhoeveelheid
            StringBuilder toppingsBuilder = new StringBuilder();

            // Gebruik isSelected() op elke JCheckBox om te controleren of deze is geselecteerd
            toppingsCheckBoxes.entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .forEach(entry -> {
                    toppingsBuilder.append(entry.getKey()).append(", ");
                    entry.getValue().setSelected(false); // Deselecteer de checkbox na gebruik
                });

            String toppings = toppingsBuilder.toString();
            if (!toppings.isEmpty()) {
                toppings = toppings.substring(0, toppings.length() - 2); // Verwijder de laatste komma en spatie
            }

            tableModel.addRow(new Object[]{selectedPizza, quantity, toppings});
        }

        private void removePizza() {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Selecteer een rij om te verwijderen.", "Waarschuwing", JOptionPane.WARNING_MESSAGE);
            }
        }

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
                order.append(dateFormat.format(new Date())); // Voeg de huidige tijd toe
            }
            return order.toString();
        }
    }

    class AddressPanel extends JPanel {
        private final JTextField nameField;
        private final JTextField addressField;
        private final JTextField cityField;
        private final JTextField postalCodeField;
        private ClientGUI gui;
    
        public AddressPanel(ClientGUI gui) {
            this.gui = gui;
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
        private ClientGUI gui;

        public ConfirmPanel(ClientGUI gui) {
            this.gui = gui;
            setLayout(new BorderLayout());
            confirmTextArea = new JTextArea();
            confirmTextArea.setEditable(false);
            add(new JScrollPane(confirmTextArea), BorderLayout.CENTER);

            JButton closeButton = new JButton("Sluit");
            closeButton.addActionListener(e -> gui.navigateToPanel("ConnectionPanel"));
            add(closeButton, BorderLayout.SOUTH);
        }

        public void setOrderDetails(String order) {
            confirmTextArea.setText(order);
        }

        public void setAddressDetails(String address) {
            confirmTextArea.append("\n\nAdresgegevens:\n" + address);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI());
    }
}
