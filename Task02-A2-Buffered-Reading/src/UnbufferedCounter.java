
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UnbufferedCounter {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Usage: java UnbufferedCounter <file-path>");
            return;
        }

        Path path = Path.of(args[0]);

        if (!Files.isRegularFile(path)) {
            System.out.println("Error: File does not exist or is not a regular file.");
            return;
        }

        long start = System.nanoTime();
        long lineCount = 0;
        long readCalls = 0;
        long totalBytes = 0;
        int lastByte = -1;

        try (FileInputStream input =
                     new FileInputStream(path.toFile())) {

            int value;

            while ((value = input.read()) != -1) {

                readCalls++;
                totalBytes++;
                lastByte = value;

                if (value == '\n') {
                    lineCount++;
                }
            }

            if (totalBytes > 0 && lastByte != '\n') {
                lineCount++;
            }

            long end = System.nanoTime();

            System.out.println("Method: Unbuffered FileInputStream");
            System.out.println("File size: " + totalBytes + " bytes");
            System.out.println("Lines: " + lineCount);
            System.out.println("Successful byte read calls: " + readCalls);
            System.out.printf("Time: %.3f ms%n",
                    (end - start) / 1_000_000.0);

        } catch (IOException e) {
            System.out.println("File reading error: " + e.getMessage());
        }
    }
}
