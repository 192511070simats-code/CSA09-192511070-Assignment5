
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BufferedCounter {

    private static final int BUFFER_SIZE = 8192;

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Usage: java BufferedCounter <file-path>");
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

        byte[] buffer = new byte[BUFFER_SIZE];

        try (BufferedInputStream input =
                     new BufferedInputStream(
                             Files.newInputStream(path), BUFFER_SIZE)) {

            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {

                readCalls++;
                totalBytes += bytesRead;

                for (int i = 0; i < bytesRead; i++) {

                    int value = buffer[i] & 0xFF;
                    lastByte = value;

                    if (value == '\n') {
                        lineCount++;
                    }
                }
            }

            if (totalBytes > 0 && lastByte != '\n') {
                lineCount++;
            }

            long end = System.nanoTime();

            System.out.println("Method: BufferedInputStream");
            System.out.println("File size: " + totalBytes + " bytes");
            System.out.println("Lines: " + lineCount);
            System.out.println("Buffer size: " + BUFFER_SIZE + " bytes");
            System.out.println("Successful buffer read calls: " + readCalls);
            System.out.printf("Time: %.3f ms%n",
                    (end - start) / 1_000_000.0);

        } catch (IOException e) {
            System.out.println("File reading error: " + e.getMessage());
        }
    }
}
