package app.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Concrete visitor implementation to calculate order statistics
public class OrderStatisticsVisitor implements OrderVisitor {
    private int totalOrders; // Total number of orders
    private int totalToppings; // Total number of toppings across all orders
    private int maxToppingsPerOrder; // Maximum number of toppings in a single order
    private Map<String, Integer> toppingCounts; // Map to store topping counts

    private final String[] pizzaNames = {"Calzone", "Diavolo", "Mozzarella"}; // Array of pizza names

    // Constructor to initialize statistics
    public OrderStatisticsVisitor() {
        totalOrders = 0;
        totalToppings = 0;
        maxToppingsPerOrder = 0;
        toppingCounts = new HashMap<>();
    }

    // Method to visit a PizzaOrder
    @Override
    public void visit(PizzaOrder order) {
        totalOrders++; // Increment total orders count
        String[] toppings = order.getOrderDescription().split("\n"); // Split order description into toppings
        totalToppings += countToppings(toppings); // Update total toppings count
        if (toppings.length - 1 > maxToppingsPerOrder) { // Update max toppings per order
            maxToppingsPerOrder = toppings.length - 1;
        }
        updateToppingCounts(toppings); // Update topping counts map
    }

    // Method to count toppings in an order
    private int countToppings(String[] toppings) {
        int count = 0;
        for (String topping : toppings) {
            if (!isPizzaName(topping)) { // Exclude pizza names
                count++;
            }
        }
        return count;
    }

    // Method to check if a string is a pizza name
    private boolean isPizzaName(String topping) {
        for (String pizzaName : pizzaNames) {
            if (topping.equalsIgnoreCase(pizzaName)) {
                return true;
            }
        }
        return false;
    }

    // Method to update topping counts map
    private void updateToppingCounts(String[] toppings) {
        for (String topping : toppings) {
            if (!isPizzaName(topping)) { // Exclude pizza names
                toppingCounts.put(topping, toppingCounts.getOrDefault(topping, 0) + 1);
            }
        }
    }

    // Getter for totalOrders
    public int getTotalOrders() {
        return totalOrders;
    }

    // Getter for averageToppingsPerOrder
    public double getAverageToppingsPerOrder() {
        if (totalOrders == 0) {
            return 0;
        }
        return (double) totalToppings / totalOrders;
    }

    // Getter for maxToppingsPerOrder
    public int getMaxToppingsPerOrder() {
        return maxToppingsPerOrder;
    }

    // Method to get most common toppings
    public List<String> getMostCommonToppings() {
        List<String> mostCommonToppings = new ArrayList<>();
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : toppingCounts.entrySet()) {
            String topping = entry.getKey();
            int count = entry.getValue();
            if (count > maxCount) {
                maxCount = count;
                mostCommonToppings.clear();
                mostCommonToppings.add(topping);
            } else if (count == maxCount) {
                mostCommonToppings.add(topping);
            }
        }
        return mostCommonToppings;
    }
}
