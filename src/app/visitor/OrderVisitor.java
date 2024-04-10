package app.visitor;

// Interface for the Visitor pattern to visit orders
public interface OrderVisitor {
    // Method to visit a PizzaOrder
    void visit(PizzaOrder order);
}
