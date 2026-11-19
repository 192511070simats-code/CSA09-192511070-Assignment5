import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class PassStoreTest {
    public static void main(String[] args) throws Exception {
        Path file = Path.of("Task06-B1-File-Store/input/buspasses.csv");
        Files.deleteIfExists(file);

        PassStore store = new PassStore(file);

        store.add("P1001", "192311001", "Arun", 12, 4500);
        Files.writeString(file, "P1002,DAMAGEDLINE\n",
                StandardOpenOption.APPEND);
        store.add("P1003", "192311003", "Divya", 7, 3800);

        System.out.println("TEST 1 - Normal pass search:");
        System.out.println(Arrays.toString(store.find("P1003")));

        System.out.println("\nTEST 2 - Missing pass ID:");
        System.out.println(Arrays.toString(store.find("P9999")));

        System.out.println("\nTEST 3 - Damaged record search:");
        System.out.println(Arrays.toString(store.find("P1002")));

        System.out.println("\nTEST 4 - List valid passes:");
        List<String[]> passes = store.list();

        for (String[] pass : passes) {
            System.out.println(Arrays.toString(pass));
        }

        System.out.println("\nTEST 5 - Add and find a new pass:");
        store.add("P1004", "192311004", "Kumar", 5, 4200);
        System.out.println(Arrays.toString(store.find("P1004")));

        System.out.println("\nValid record count: " + store.list().size());
        System.out.println("B1 file-store tests completed.");
    }
}
