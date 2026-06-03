package business;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(
                System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/ProductManagement"),
                System.getenv().getOrDefault("DB_USER", "root"),
                System.getenv().getOrDefault("DB_PASSWORD", "")
        );
    }
}
