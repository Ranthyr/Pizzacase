package app.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final String DATABASE_NAME = "your_database.db"; // Naam van de SQLite-database
    private static final String CONNECTION_STRING = "jdbc:sqlite:" + DATABASE_NAME;
    private static Server instance;
    private Connection connection;

    private Server() {
        // Verbinding maken met de SQLite-database
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public void start() {
        // Server starten (voor toekomstig gebruik)
    }

    public void saveOrder(String orderDetails) {
        // Bestelling opslaan in de database
        String sql = "INSERT INTO orders (details) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, orderDetails);
            statement.executeUpdate();
            System.out.println("Order saved successfully to SQLite database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getOrders() {
        // Bestellingen ophalen uit de database
        List<String> orders = new ArrayList<>();
        String sql = "SELECT details FROM orders";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String order = resultSet.getString("details");
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}
