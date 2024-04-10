package app.visitor;

// Interface representing an order
public interface Order {
    // Method to accept an OrderVisitor
    void accept(OrderVisitor visitor);
}
