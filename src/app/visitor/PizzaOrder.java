package app.visitor;

// Concrete implementation of Order representing a pizza order
public class PizzaOrder implements Order {
    private String customerName;
    private String address;
    private String city;
    private String postalCode;
    private String orderDescription;
    private String orderTime;

    // Constructor to initialize a PizzaOrder instance
    public PizzaOrder(String customerName, String address, String city, String postalCode, String orderDescription, String orderTime) {
        this.customerName = customerName;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.orderDescription = orderDescription;
        this.orderTime = orderTime;
    }

    // Method to accept an OrderVisitor
    public void accept(OrderVisitor visitor) {
        visitor.visit(this); // Calls visit method of the OrderVisitor
    }

    // Getter methods for various order details
    public String getCustomerName() {
        return customerName;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public String getOrderTime() {
        return orderTime;
    }
}
