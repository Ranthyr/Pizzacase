package app.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Server {
    private static Server instance;
    private Connection connection;

    // Private constructor to prevent instantiation from outside
    private Server() {
        try {
            // Verbinding maken met de MySQL-database
            connection = DriverManager.getConnection("jdbc:mysql://62.72.177.23:3306/s510_Pizzacase", "u510_VKj6VE7tj9", ".nhTFZw+TywKM10ovO8T.g3P");
            System.out.println("Verbonden met de database.");

            // Maak de tabellen aan als ze nog niet bestaan
            createTables();
        } catch (SQLException e) {
            handleSQLException("Kan geen verbinding maken met de database.", e);
        }
    }

    // Static method to get the instance of Server class
    public static synchronized Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    // Methode om een bestelling op te slaan in de database
    public void saveOrderToDatabase(String customerName, String address, String orderDescription, String orderTime) {
        System.out.println("Test2");
        if (connection == null) {
            System.err.println("Databaseverbinding is niet beschikbaar.");
            return;
        }

        try {
            System.out.println("Test");
            // SQL-query om de bestelling in te voegen in de database
            String sql = "INSERT INTO orders (customer_name, address, order_details, order_time) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, customerName);
            statement.setString(2, address);
            statement.setString(3, orderDescription);
            statement.setString(4, orderTime);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Bestelling succesvol opgeslagen in de database.");
            } else {
                System.err.println("Geen rijen ingevoegd in de database.");
            }
        } catch (SQLException e) {
            handleSQLException("Fout bij het opslaan van de bestelling in de database.", e);
        }
    }

    // Methode om de verbinding met de database te sluiten
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

    // Methode om de databaseverbinding op te halen
    public Connection getConnection() {
        return connection;
    }

    // Methode om de tabellen aan te maken als ze nog niet bestaan
    private void createTables() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS orders (id INT AUTO_INCREMENT PRIMARY KEY, customer_name VARCHAR(255), address TEXT, order_details TEXT, order_time VARCHAR(255))");
            // Voeg hier meer SQL-opdrachten toe om andere tabellen aan te maken indien nodig
        } catch (SQLException e) {
            handleSQLException("Fout bij het aanmaken van tabellen.", e);
        }
    }

    // Methode om SQLException af te handelen
    private void handleSQLException(String message, SQLException e) {
        System.err.println(message + " Error: " + e.getMessage());
        e.printStackTrace(); // Print de stacktrace voor gedetailleerde foutinformatie
    }
}
