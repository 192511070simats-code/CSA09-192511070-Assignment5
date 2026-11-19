import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class PassStoreGrowthTest {
    public static void main(String[] args) throws Exception {
        int[] sizes = {1000, 10000, 50000, 100000};
        Path file = Path.of("Task06-B1-File-Store/input/growth.csv");

        System.out.println("Records,Search ID,Average Search Time (ms)");

        for (int size : sizes) {
            try (BufferedWriter writer = Files.newBufferedWriter(file)) {
                for (int i = 1; i <= size; i++) {
                    writer.write(String.format(
                            "P%06d,REG%06d,Student%d,%d,4500.0",
                            i, i, i, (i % 20) + 1));
                    writer.newLine();
                }
            }

            PassStore store = new PassStore(file);
            String target = String.format("P%06d", size);

            // Warm-up searches
            for (int i = 0; i < 5; i++) {
                store.find(target);
            }

            int repetitions = 20;
            long start = System.nanoTime();

            for (int i = 0; i < repetitions; i++) {
                store.find(target);
            }

            long end = System.nanoTime();
            double averageMs =
                    (end - start) / 1_000_000.0 / repetitions;

            System.out.printf("%d,%s,%.4f%n",
                    size, target, averageMs);
        }

        System.out.println("Growth measurement completed.");
    }
}
