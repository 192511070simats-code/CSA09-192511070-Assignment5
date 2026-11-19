import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class B4DatabaseSetup {
    public static void main(String[] args) throws Exception {
        System.out.print("Enter MySQL root password: ");
        String password = new Scanner(System.in).nextLine();

        String url = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";

        try (Connection con = DriverManager.getConnection(url, "root", password);
             Statement st = con.createStatement()) {

            st.executeUpdate("CREATE DATABASE IF NOT EXISTS b4_migration_test");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS b4_migration_test.bus_pass (" +
                "pass_id VARCHAR(20) PRIMARY KEY, " +
                "reg_no VARCHAR(20) NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "route_no INT NOT NULL, " +
                "amount DECIMAL(10,2) NOT NULL" +
                ") ENGINE=InnoDB"
            );

            st.executeUpdate("DELETE FROM b4_migration_test.bus_pass");

            System.out.println("PASS: B4 database and InnoDB table are ready.");
        }
    }
}
