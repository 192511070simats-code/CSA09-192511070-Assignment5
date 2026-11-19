import java.sql.*;
import java.util.Scanner;

public class B2LoginSecurityTest {
    private static final String URL =
        "jdbc:mysql://localhost:3306/b2_security_test";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter MySQL root password: ");
            String dbPassword = scanner.nextLine();

            String craftedId = "' OR '1'='1' -- ";
            String wrongPassword = "wrong";

            try (Connection con = DriverManager.getConnection(
                    URL, "root", dbPassword)) {

                String unsafeSql =
                    "SELECT * FROM demo_users WHERE id='" + craftedId +
                    "' AND pwd='" + wrongPassword + "'";

                System.out.println("\nCrafted username: " + craftedId);
                System.out.println("Supplied password: " + wrongPassword);

                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery(unsafeSql)) {
                    System.out.println(
                        "Unsafe login accepted: " + rs.next());
                }

                String safeSql =
                    "SELECT * FROM demo_users WHERE id=? AND pwd=?";

                try (PreparedStatement ps =
                         con.prepareStatement(safeSql)) {
                    ps.setString(1, craftedId);
                    ps.setString(2, wrongPassword);

                    try (ResultSet rs = ps.executeQuery()) {
                        System.out.println(
                            "PreparedStatement login accepted: " + rs.next());
                    }
                }

                System.out.println(
                    "\nExpected: unsafe=true, PreparedStatement=false");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
