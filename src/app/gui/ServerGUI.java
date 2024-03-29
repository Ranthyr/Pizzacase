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

        frame = new JFrame("Pizza Bestellingen - Restaurant");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        panel = new JPanel(new GridLayout(0, 1));
        frame.add(panel);

        ordersTextArea = new JTextArea();
        ordersTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(ordersTextArea);
        panel.add(scrollPane);

        frame.setVisible(true);
    }

    public void addOrder(String order) {
        ordersList.add(order);
        updateOrdersTextArea();
    }

    private void updateOrdersTextArea() {
        ordersTextArea.setText("");
        for (String order : ordersList) {
            ordersTextArea.append(order + "\n\n");
        }
    }
}