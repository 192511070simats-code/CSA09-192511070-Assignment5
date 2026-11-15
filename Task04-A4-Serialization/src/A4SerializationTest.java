import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class A4SerializationTest {
    public static void main(String[] args) {
        Path file = Path.of("Task04-A4-Serialization",
                            "data", "hostel-data.ser");

        try {
            Hostel hostel = new Hostel("SIMATS Hostel");

            Room room1 = new Room(101);
            room1.addResident(new Resident(
                "Arun", "192511071", "secret123",
                "9876543210", null
            ));
            room1.addResident(new Resident(
                "Priya", "192511072", "password456",
                "9876501234", null
            ));

            Room room2 = new Room(102);
            room2.addResident(new Resident(
                "Kumar", "192511073", "pass789",
                "9876512345", null
            ));

            hostel.addRoom(room1);
            hostel.addRoom(room2);

            try (ObjectOutputStream output =
                     new ObjectOutputStream(
                         new FileOutputStream(file.toFile()))) {
                output.writeObject(hostel);
            }

            System.out.println("PASS: Hostel object graph serialized.");

            Hostel restoredHostel;

            try (ObjectInputStream input =
                     new ObjectInputStream(
                         new FileInputStream(file.toFile()))) {
                restoredHostel = (Hostel) input.readObject();
            }

            System.out.println("\nRestored hostel: " +
                               restoredHostel.getHostelName());

            int residentCount = 0;

            for (Room room : restoredHostel.getRooms()) {
                System.out.println("\nRoom number: " +
                                   room.getRoomNumber());

                for (Resident resident : room.getResidents()) {
                    residentCount++;
                    System.out.println("Name: " + resident.getName());
                    System.out.println("Register number: " +
                                       resident.getRegisterNumber());
                    System.out.println("Phone: " +
                                       resident.getPhoneNumber());
                    System.out.println("Password after loading: " +
                                       resident.getPassword());
                    System.out.println("Database connection after loading: " +
                                       resident.getDatabaseConnection());
                }
            }

            System.out.println("\nTotal rooms: " +
                               restoredHostel.getRooms().size());
            System.out.println("Total residents: " + residentCount);
            System.out.println("Serialized file size: " +
                               Files.size(file) + " bytes");
            System.out.println("\nSerialization test completed.");

        } catch (Exception e) {
            System.out.println("Serialization test failed: " +
                               e.getMessage());
            e.printStackTrace();
        }
    }
}
