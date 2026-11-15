import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class OldWriter {
    public static void main(String[] args) throws Exception {
        VersionedResident resident =
            new VersionedResident("Anu", "192511080");

        try (ObjectOutputStream output = new ObjectOutputStream(
                new FileOutputStream(
                    "Task04-A4-Serialization/data/old-resident.ser"))) {
            output.writeObject(resident);
        }

        System.out.println("Old resident file created successfully.");
    }
}
