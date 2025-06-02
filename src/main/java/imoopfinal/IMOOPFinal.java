package imoopfinal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class IMOOPFinal {

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            createTables(conn);
            System.out.println("Database initialized.");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }

        javax.swing.SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }

    private static void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
    "id INT AUTO_INCREMENT PRIMARY KEY," +
    "username VARCHAR(50) NOT NULL UNIQUE," +
    "password VARCHAR(255) NOT NULL," +
    "email VARCHAR(100) NOT NULL," +
    "firstname VARCHAR(50) NOT NULL," +
    "lastname VARCHAR(50) NOT NULL," +
    "department VARCHAR(100) NOT NULL," +
    "usertype VARCHAR(20) NOT NULL)");

stmt.execute("CREATE TABLE IF NOT EXISTS schedule (" +
    "id INT AUTO_INCREMENT PRIMARY KEY," +
    "teacher VARCHAR(100) NOT NULL," +
    "department VARCHAR(100) NOT NULL," +
    "date DATE NOT NULL," +
    "time TIME NOT NULL," +
    "year VARCHAR(4) NOT NULL," +
    "month VARCHAR(10) NOT NULL)"+
    "slots INT NOT NULL");

stmt.execute("CREATE TABLE IF NOT EXISTS appointments (" +
    "id INT AUTO_INCREMENT PRIMARY KEY," +
    "student VARCHAR(100) NOT NULL," +
    "teacher VARCHAR(100) NOT NULL," +
    "date DATE NOT NULL," +
    "time TIME NOT NULL," +
    "year VARCHAR(4) NOT NULL," +
    "course VARCHAR(100) NOT NULL," +
    "month VARCHAR(10) NOT NULL)");

        }
    }

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/imoopdb"; // Make sure 'imoopdb' exists
        String user = "root"; // Your MySQL username
        String password = ""; // Default password for XAMPP's MySQL root is empty
        return DriverManager.getConnection(url, user, password);
    }
}
