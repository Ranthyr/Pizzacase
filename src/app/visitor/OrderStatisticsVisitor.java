package app.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderStatisticsVisitor implements OrderVisitor {
    private int totalOrders;
    private int totalToppings; // Aangepast naar het totale aantal toppings
    private int maxToppingsPerOrder;
    private Map<String, Integer> toppingCounts;

    private final String[] pizzaNames = {"Calzone", "Diavolo", "Mozzarella"};

    public OrderStatisticsVisitor() {
        totalOrders = 0;
        totalToppings = 0;
        maxToppingsPerOrder = 0;
        toppingCounts = new HashMap<>();
    }

    @Override
    public void visit(PizzaOrder order) {
        totalOrders++;
        String[] toppings = order.getOrderDescription().split("\n");
        totalToppings += countToppings(toppings); // Totaal aantal toppings bijwerken zonder pizza naam
        if (toppings.length - 1 > maxToppingsPerOrder) {
            maxToppingsPerOrder = toppings.length - 1;
        }
        updateToppingCounts(toppings);
    }

    private int countToppings(String[] toppings) {
        int count = 0;
        for (String topping : toppings) {
            if (!isPizzaName(topping)) { // Controleren of het geen pizza-naam is
                count++;
            }
        }
        return count;
    }

    private boolean isPizzaName(String topping) {
        for (String pizzaName : pizzaNames) {
            if (topping.equalsIgnoreCase(pizzaName)) {
                return true;
            }
        }
        return false;
    }

    private void updateToppingCounts(String[] toppings) {
        for (String topping : toppings) {
            if (!isPizzaName(topping)) { // Controleren of het geen pizza-naam is
                toppingCounts.put(topping, toppingCounts.getOrDefault(topping, 0) + 1);
            }
        }
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public double getAverageToppingsPerOrder() {
        if (totalOrders == 0) {
            return 0;
        }
        return (double) totalToppings / totalOrders; // Gemiddeld aantal toppings per bestelling berekenen
    }

    public int getMaxToppingsPerOrder() {
        return maxToppingsPerOrder;
    }

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








