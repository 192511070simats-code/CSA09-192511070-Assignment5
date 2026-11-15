import java.io.Serializable;

public class VersionedResident implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String registerNumber;

    public VersionedResident(String name, String registerNumber) {
        this.name = name;
        this.registerNumber = registerNumber;
    }

    public String getName() {
        return name;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }
}
