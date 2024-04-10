package app.gui;

import app.visitor.Order;
import app.visitor.PizzaOrder;
import app.visitor.OrderStatisticsVisitor;
import app.server.Server;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ServerGUI {
    private JFrame frame;
    private JTextArea orderListTextArea;

    public ServerGUI() {
        initializeGUI();
        fetchOrdersFromDatabase();
        displayOrderStatistics();
    }

    private void initializeGUI() {
        frame = new JFrame("Server - Bestellingen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        orderListTextArea = new JTextArea();
        orderListTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(orderListTextArea);

        frame.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Vernieuwen");
        refreshButton.addActionListener(e -> fetchOrdersFromDatabase());
        frame.add(refreshButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void fetchOrdersFromDatabase() {
        List<PizzaOrder> orders = Server.getInstance().fetchOrders();
        StringBuilder ordersText = new StringBuilder();
        for (Order order : orders) {
            ordersText.append("Klant: ").append(((PizzaOrder) order).getCustomerName()).append("\n")
                    .append("Adres: ").append(((PizzaOrder) order).getAddress()).append("\n")
                    .append("Stad: ").append(((PizzaOrder) order).getCity()).append("\n")
                    .append("Postcode: ").append(((PizzaOrder) order).getPostalCode()).append("\n")
                    .append("Bestelling: ").append(((PizzaOrder) order).getOrderDescription()).append("\n")
                    .append("Tijd: ").append(((PizzaOrder) order).getOrderTime()).append("\n\n");
        }
        orderListTextArea.setText(ordersText.toString());
    }

    private void displayOrderStatistics() {
        OrderStatisticsVisitor statisticsVisitor = new OrderStatisticsVisitor();
        List<PizzaOrder> orders = Server.getInstance().fetchOrders();
        for (Order order : orders) {
            order.accept(statisticsVisitor);
        }

        // Display statistics in JOptionPane (you can customize this part according to your requirement)
        String statisticsMessage = "Totaal aantal bestellingen: " + statisticsVisitor.getTotalOrders() + "\n" +
                "Gemiddeld aantal toppings per bestelling: " + statisticsVisitor.getAverageToppingsPerOrder() + "\n" +
                "Meest voorkomende toppings: " + statisticsVisitor.getMostCommonToppings() + "\n" +
                "Maximaal aantal toppings per bestelling: " + statisticsVisitor.getMaxToppingsPerOrder();
        JOptionPane.showMessageDialog(frame, statisticsMessage, "Statistieken", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}
