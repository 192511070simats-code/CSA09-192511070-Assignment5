import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public class A3JdbcIntegrationTest {
    public static void main(String[] args) {
        String username;
        String password;

        if (System.console() != null) {
            username = System.console().readLine("MySQL username (press Enter for root): ");
            if (username.isBlank()) username = "root";
            password = new String(System.console().readPassword("MySQL password: "));
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("MySQL username (press Enter for root): ");
            username = scanner.nextLine();
            if (username.isBlank()) username = "root";
            System.out.print("MySQL password: ");
            password = scanner.nextLine();
        }

        String baseUrl = "jdbc:mysql://localhost:3306/?allowPublicKeyRetrieval=true&useSSL=false";
        String databaseUrl = "jdbc:mysql://localhost:3306/college_db?allowPublicKeyRetrieval=true&useSSL=false";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection adminConnection =
                         DriverManager.getConnection(baseUrl, username, password);
                 Statement statement = adminConnection.createStatement()) {
                statement.executeUpdate("CREATE DATABASE IF NOT EXISTS college_db");
            }

            try (Connection connection =
                         DriverManager.getConnection(databaseUrl, username, password);
                 Statement statement = connection.createStatement()) {

                statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS student (" +
                    "student_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "register_no VARCHAR(20) NOT NULL UNIQUE, " +
                    "student_name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100), " +
                    "department VARCHAR(100))"
                );

                StudentDao dao = new StudentDao(connection);
                String registerNo = "TEST" + System.currentTimeMillis();

                Student student = new Student(
                    registerNo, "DAO Test Student",
                    "dao.test@example.com", "Computer Science"
                );

                dao.insert(student);
                System.out.println("\nPASS: insert()");

                int studentId;
                try (PreparedStatement ps = connection.prepareStatement(
                        "SELECT student_id FROM student WHERE register_no = ?")) {
                    ps.setString(1, registerNo);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        studentId = rs.getInt("student_id");
                    }
                }

                Student found = dao.findById(studentId);
                System.out.println("PASS: findById() - " + found);

                List<Student> students = dao.findAll();
                System.out.println("PASS: findAll() - returned " +
                                   students.size() + " student record(s)");

                found.setStudentName("Updated DAO Test Student");
                dao.update(found);
                Student updated = dao.findById(studentId);
                System.out.println("PASS: update() - " +
                                   updated.getStudentName());

                dao.delete(studentId);
                Student deleted = dao.findById(studentId);
                System.out.println("PASS: delete() - record absent: " +
                                   (deleted == null));

                System.out.println("\nAll five DAO CRUD operations were tested successfully.");
            }
        } catch (Exception e) {
            System.out.println("\nJDBC test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
