import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class B3Connection {
    private static final String URL =
        "jdbc:mysql://localhost:3306/b3_jdbc_test?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";

    public static Connection getConnection() throws SQLException {
        System.out.print("Enter MySQL root password: ");
        String password = new Scanner(System.in).nextLine();
        return DriverManager.getConnection(URL, "root", password);
    }
}

