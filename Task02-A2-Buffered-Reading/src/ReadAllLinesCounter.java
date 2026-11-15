
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ReadAllLinesCounter {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println(
                    "Usage: java ReadAllLinesCounter <file-path>");
            return;
        }

        Path path = Path.of(args[0]);

        if (!Files.isRegularFile(path)) {
            System.out.println(
                    "Error: File does not exist or is not a regular file.");
            return;
        }

        try {
            long start = System.nanoTime();

            List<String> lines = Files.readAllLines(path);

            long lineCount = lines.size();

            long end = System.nanoTime();

            System.out.println("Method: Files.readAllLines()");
            System.out.println("File size: " + Files.size(path) + " bytes");
            System.out.println("Lines: " + lineCount);
            System.out.println("Memory: Entire list of lines loaded into memory");
            System.out.printf("Time: %.3f ms%n",
                    (end - start) / 1_000_000.0);

        } catch (IOException e) {
            System.out.println("File reading error: " + e.getMessage());
        }
    }
}
