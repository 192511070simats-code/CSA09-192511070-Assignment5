import java.io.Serializable;

public class VersionedResident implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String registerNumber;
    private String phoneNumber;

    public String getName() {
        return name;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
