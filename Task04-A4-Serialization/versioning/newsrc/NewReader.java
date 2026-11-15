import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class NewReader {
    public static void main(String[] args) throws Exception {
        try (ObjectInputStream input = new ObjectInputStream(
                new FileInputStream(
                    "Task04-A4-Serialization/data/old-resident.ser"))) {

            VersionedResident resident =
                (VersionedResident) input.readObject();

            System.out.println("Old file loaded using the new class.");
            System.out.println("Name: " + resident.getName());
            System.out.println("Register number: " +
                               resident.getRegisterNumber());
            System.out.println("New phone number field: " +
                               resident.getPhoneNumber());
        }
    }
}
