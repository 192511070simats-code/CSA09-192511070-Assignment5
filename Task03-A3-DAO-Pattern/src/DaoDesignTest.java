import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class DaoDesignTest {

    public static void main(String[] args) {

        // Check whether StudentDao implements the generic Dao interface
        boolean implementsDao = Dao.class.isAssignableFrom(StudentDao.class);

        System.out.println("StudentDao implements Dao: " + implementsDao);

        // Check the five required methods
        List<String> expectedMethods = Arrays.asList(
                "insert", "findById", "findAll", "update", "delete"
        );

        for (String methodName : expectedMethods) {
            boolean found = Arrays.stream(Dao.class.getDeclaredMethods())
                    .map(Method::getName)
                    .anyMatch(methodName::equals);

            System.out.println(methodName + " method exists: " + found);
        }

        // Test Student object and its fields
        Student student = new Student(
                101,
                "192511070",
                "Test Student",
                "student@example.com",
                "Computer Science"
        );

        System.out.println("\nStudent object mapping test:");
        System.out.println("Student ID: " + student.getStudentId());
        System.out.println("Register No: " + student.getRegisterNo());
        System.out.println("Name: " + student.getStudentName());
        System.out.println("Email: " + student.getEmail());
        System.out.println("Department: " + student.getDepartment());

        System.out.println("\nDAO design verification completed.");
    }
}
