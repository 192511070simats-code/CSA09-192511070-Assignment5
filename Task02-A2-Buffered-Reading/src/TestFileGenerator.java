
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestFileGenerator {

    private static final byte[] LINE =
            "A2 test data for measuring Java file reading performance.\n"
                    .getBytes(java.nio.charset.StandardCharsets.UTF_8);

    public static void main(String[] args) {

        Path inputDirectory = Path.of(
                "Task02-A2-Buffered-Reading", "input");

        int[] sizesMB = {1, 10, 50};

        try {
            Files.createDirectories(inputDirectory);

            for (int sizeMB : sizesMB) {

                Path file = inputDirectory.resolve(
                        "test-" + sizeMB + "mb.txt");

                long targetBytes = sizeMB * 1024L * 1024L;
                long writtenBytes = 0;
                long lineCount = 0;

                try (BufferedOutputStream output =
                             new BufferedOutputStream(
                                     Files.newOutputStream(file))) {

                    while (writtenBytes + LINE.length <= targetBytes) {
                        output.write(LINE);
                        writtenBytes += LINE.length;
                        lineCount++;
                    }

                    // Write remaining bytes to reach the exact file size.
                    int remaining = (int) (targetBytes - writtenBytes);

                    if (remaining > 0) {
                        output.write(LINE, 0, remaining);
                        lineCount++;
                    }
                }

                System.out.println(
                        sizeMB + " MB test file created: "
                                + Files.size(file) + " bytes, "
                                + lineCount + " lines");
            }

        } catch (IOException e) {
            System.out.println("File generation error: " + e.getMessage());
        }
    }
}
