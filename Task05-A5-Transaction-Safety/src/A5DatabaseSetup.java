import java.sql.*;
import java.util.Scanner;

public class A5DatabaseSetup {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            String serverUrl = "jdbc:mysql://localhost:3306/";
            try (Connection server = DriverManager.getConnection(serverUrl, "root", password);
                 Statement st = server.createStatement()) {
                st.executeUpdate("CREATE DATABASE IF NOT EXISTS assignment5_db");
            }

            String dbUrl = "jdbc:mysql://localhost:3306/assignment5_db";
            try (Connection con = DriverManager.getConnection(dbUrl, "root", password);
                 Statement st = con.createStatement()) {

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS wallets (
                        wallet_id VARCHAR(20) PRIMARY KEY,
                        balance DECIMAL(12,2) NOT NULL
                    )
                    """);

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS demo_users (
                        id VARCHAR(50) PRIMARY KEY,
                        pwd VARCHAR(100) NOT NULL
                    )
                    """);

                st.executeUpdate("""
                    INSERT INTO wallets(wallet_id, balance)
                    VALUES ('HOSTEL', 1000.00), ('CANTEEN', 200.00)
                    ON DUPLICATE KEY UPDATE balance = VALUES(balance)
                    """);

                st.executeUpdate("""
                    INSERT INTO demo_users(id, pwd)
                    VALUES ('student1', 'correct123')
                    ON DUPLICATE KEY UPDATE pwd = VALUES(pwd)
                    """);
            }

            System.out.println("PASS: Database and tables created.");
            System.out.println("Hostel wallet opening balance: Rs. 1000.00");
            System.out.println("Canteen wallet opening balance: Rs. 200.00");
            System.out.println("Sample login account created: student1");
        } catch (SQLException e) {
            System.out.println("Database setup failed: " + e.getMessage());
        }
    }
}
