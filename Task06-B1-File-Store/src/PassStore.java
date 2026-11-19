import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class PassStore {
    private final Path file;

    public PassStore(Path file) {
        this.file = file;
    }

    public void add(String passId, String regNo, String name,
                    int route, double amount) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                file, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write(String.join(",",
                    passId, regNo, name,
                    String.valueOf(route),
                    String.valueOf(amount)));
            writer.newLine();
        }
    }

    public String[] find(String passId) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);

                if (fields.length != 5) {
                    continue;
                }

                if (fields[0].equals(passId)) {
                    return fields;
                }
            }
        }

        return null;
    }

    public List<String[]> list() throws IOException {
        List<String[]> passes = new ArrayList<>();

        if (!Files.exists(file)) {
            return passes;
        }

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);

                if (fields.length == 5) {
                    passes.add(fields);
                }
            }
        }

        return passes;
    }
}
