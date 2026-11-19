import java.sql.*;
import java.util.Scanner;

public class B2DatabaseSetup {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            String serverUrl = "jdbc:mysql://localhost:3306/";

            try (Connection con = DriverManager.getConnection(
                    serverUrl, "root", password);
                 Statement st = con.createStatement()) {

                st.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS b2_security_test");

                st.executeUpdate("USE b2_security_test");

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS demo_users (
                        id VARCHAR(50) PRIMARY KEY,
                        pwd VARCHAR(100) NOT NULL
                    )
                    """);

                st.executeUpdate("DELETE FROM demo_users");

                st.executeUpdate("""
                    INSERT INTO demo_users(id, pwd)
                    VALUES ('student1', 'correct123')
                    """);

                System.out.println(
                    "PASS: B2 test database and demo account created.");
                System.out.println("Test username: student1");
            }
        } catch (SQLException e) {
            System.out.println("Database setup failed: " + e.getMessage());
        }
    }
}
