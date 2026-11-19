import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class B2PassStoreTest {

    private static final Path FILE =
        Path.of("Task07-B2-AdversarialInput/input/damaged-passes.csv");

    // Reproduces the basic five-field check used in B1.
    private static boolean originalB1Accepts(String line) {
        return line.split(",", -1).length == 5;
    }

    // Stronger validation for B2.
    private static boolean isValidRecord(String line) {
        if (line == null || line.isBlank()) {
            return false;
        }

        String[] fields = line.split(",", -1);

        if (fields.length != 5) {
            return false;
        }

        for (String field : fields) {
            if (field.isBlank()) {
                return false;
            }
        }

        try {
            Integer.parseInt(fields[3].trim());
            double amount = Double.parseDouble(fields[4].trim());

            return amount >= 0 && Double.isFinite(amount);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static List<String> readValidRecords(Path file)
            throws IOException {
        List<String> validRecords = new ArrayList<>();

        try (BufferedReader reader =
                 Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    line = line.replaceFirst("^\\uFEFF", "");
                    firstLine = false;
                }

                if (isValidRecord(line)) {
                    validRecords.add(line);
                }
            }
        }

        return validRecords;
    }

    public static void main(String[] args) {
        try {
            System.out.println("B1 original five-field check:");
            System.out.println("--------------------------------");

            try (BufferedReader reader =
                     Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
                String line;
                int row = 0;

                while ((line = reader.readLine()) != null) {
                    row++;
                    if (row == 1) {
                        line = line.replaceFirst("^\\uFEFF", "");
                    }

                    System.out.println(
                        "Line " + row + ": " +
                        (originalB1Accepts(line) ? "ACCEPTED" : "REJECTED"));
                }
            }

            System.out.println("\nB2 stronger validation:");
            System.out.println("-----------------------");

            List<String> validRecords = readValidRecords(FILE);

            for (String record : validRecords) {
                System.out.println("VALID: " + record);
            }

            System.out.println(
                "\nValid records accepted: " + validRecords.size());
            System.out.println(
                "Damaged or empty records rejected: " +
                (7 - validRecords.size()));
            System.out.println("B2 file validation test completed.");

        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }
}
