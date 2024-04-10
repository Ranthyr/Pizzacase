package app.visitor;

public interface Order {
    void accept(OrderVisitor visitor);
}
