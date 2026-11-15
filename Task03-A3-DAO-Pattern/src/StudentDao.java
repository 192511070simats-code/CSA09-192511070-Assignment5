import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDao implements Dao<Student, Integer> {

    private final Connection connection;

    public StudentDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Student student) throws SQLException {
        String sql = "INSERT INTO student " +
                "(register_no, student_name, email, department) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.getRegisterNo());
            statement.setString(2, student.getStudentName());
            statement.setString(3, student.getEmail());
            statement.setString(4, student.getDepartment());
            statement.executeUpdate();
        }
    }

    @Override
    public Student findById(Integer id) throws SQLException {
        String sql = "SELECT student_id, register_no, student_name, " +
                "email, department FROM student WHERE student_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapRow(result);
                }
            }
        }

        return null;
    }

    @Override
    public List<Student> findAll() throws SQLException {
        String sql = "SELECT student_id, register_no, student_name, " +
                "email, department FROM student";

        List<Student> students = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                students.add(mapRow(result));
            }
        }

        return students;
    }

    @Override
    public void update(Student student) throws SQLException {
        String sql = "UPDATE student SET register_no = ?, " +
                "student_name = ?, email = ?, department = ? " +
                "WHERE student_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.getRegisterNo());
            statement.setString(2, student.getStudentName());
            statement.setString(3, student.getEmail());
            statement.setString(4, student.getDepartment());
            statement.setInt(5, student.getStudentId());

            statement.executeUpdate();
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM student WHERE student_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private Student mapRow(ResultSet result) throws SQLException {
        return new Student(
                result.getInt("student_id"),
                result.getString("register_no"),
                result.getString("student_name"),
                result.getString("email"),
                result.getString("department")
        );
    }
}
