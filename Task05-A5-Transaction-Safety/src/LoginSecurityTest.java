import java.sql.*;
import java.util.Scanner;

public class LoginSecurityTest {
    private static final String URL =
        "jdbc:mysql://localhost:3306/assignment5_db";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            try (Connection con =
                     DriverManager.getConnection(URL, "root", password)) {

                String id = "' OR '1'='1' -- ";
                String wrongPassword = "wrong";

                System.out.println("\nCrafted user ID: " + id);
                System.out.println("Incorrect password: " + wrongPassword);

                String unsafeSql =
                    "SELECT * FROM demo_users WHERE id='" + id +
                    "' AND pwd='" + wrongPassword + "'";

                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery(unsafeSql)) {
                    System.out.println("\nUnsafe concatenated query:");
                    System.out.println(unsafeSql);
                    System.out.println("Unsafe login accepted: " + rs.next());
                }

                String safeSql =
                    "SELECT * FROM demo_users WHERE id=? AND pwd=?";

                try (PreparedStatement ps =
                         con.prepareStatement(safeSql)) {
                    ps.setString(1, id);
                    ps.setString(2, wrongPassword);

                    try (ResultSet rs = ps.executeQuery()) {
                        System.out.println("\nPreparedStatement query:");
                        System.out.println("Safe login accepted: " + rs.next());
                    }
                }

                System.out.println(
                    "\nPASS: PreparedStatement treated the crafted input as data.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
