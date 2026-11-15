import java.io.Serializable;
import java.sql.Connection;

public class Resident implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String registerNumber;
    private transient String password;
    private String phoneNumber;
    private transient Connection databaseConnection;

    public Resident(String name, String registerNumber,
                    String password, String phoneNumber,
                    Connection databaseConnection) {
        this.name = name;
        this.registerNumber = registerNumber;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.databaseConnection = databaseConnection;
    }

    public String getName() {
        return name;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Connection getDatabaseConnection() {
        return databaseConnection;
    }
}
