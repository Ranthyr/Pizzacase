package app.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import app.visitor.PizzaOrder;

public class Server {
    private static Server instance;
    private Connection connection;

    private Server() {
        try {
            // Verbinding maken met de MySQL-database
            connection = DriverManager.getConnection("jdbc:mysql://62.72.177.23:3306/s510_Pizzacase", "u510_VKj6VE7tj9",
                    ".nhTFZw+TywKM10ovO8T.g3P");
            System.out.println("Verbonden met de database.");
            createTables();
        } catch (SQLException e) {
            handleSQLException("Kan geen verbinding maken met de database.", e);
        }
    }

    public static synchronized Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public boolean saveOrderToDatabase(String customerName, String address, String city, String postalCode, String orderDescription, String orderTime) {
        if (connection == null) {
            System.err.println("Databaseverbinding is niet beschikbaar.");
            return false;
        }
    
        // Zorg ervoor dat de SQL-query overeenkomt met je database schema
        String sql = "INSERT INTO orders (customer_name, address, city, postal_code, order_details, order_time) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerName);
            statement.setString(2, address);
            statement.setString(3, city);
            statement.setString(4, postalCode); // Zorg ervoor dat deze in de juiste volgorde staan
            statement.setString(5, orderDescription);
            statement.setString(6, orderTime);
    
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Bestelling succesvol opgeslagen in de database.");
                return true;
            } else {
                System.err.println("Geen rijen ingevoegd in de database.");
                return false;
            }
        } catch (SQLException e) {
            handleSQLException("Fout bij het opslaan van de bestelling in de database.", e);
            return false;
        }
    }
    

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Databaseverbinding gesloten.");
            }
        } catch (SQLException e) {
            handleSQLException("Fout bij het sluiten van de databaseverbinding.", e);
        }
    }

    private void createTables() {
        try (Statement statement = connection.createStatement()) {
            String sqlCreate = "CREATE TABLE IF NOT EXISTS orders (" +
                               "id INT AUTO_INCREMENT PRIMARY KEY, " +
                               "customer_name VARCHAR(255), " +
                               "address TEXT, " +
                               "city VARCHAR(255), " + // Toevoegen van city kolom
                               "postal_code VARCHAR(20), " + // Toevoegen van postal_code kolom
                               "order_details TEXT, " +
                               "order_time VARCHAR(255))";
            statement.executeUpdate(sqlCreate);
            System.out.println("Tabel 'orders' is aangemaakt of bestaat al.");
        } catch (SQLException e) {
            handleSQLException("Fout bij het aanmaken van tabellen.", e);
        }
    }
    

    private void handleSQLException(String message, SQLException e) {
        System.err.println(message + " Error: " + e.getMessage());
        e.printStackTrace();
    }

    public Connection getConnection() {
        return connection;
    }

    public List<PizzaOrder> fetchOrders() {
        List<PizzaOrder> orders = new ArrayList<>();
        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM orders")) {
            while (resultSet.next()) {
                String customerName = resultSet.getString("customer_name");
                String address = resultSet.getString("address");
                String city = resultSet.getString("city");
                String postalCode = resultSet.getString("postal_code");
                String orderDetails = resultSet.getString("order_details");
                String orderTime = resultSet.getString("order_time");

                orders.add(new PizzaOrder(customerName, address, city, postalCode, orderDetails, orderTime));
            }
        } catch (SQLException e) {
            System.err.println("Fout bij het ophalen van bestellingen uit de database: " + e.getMessage());
        }
        return orders;
    }
}
