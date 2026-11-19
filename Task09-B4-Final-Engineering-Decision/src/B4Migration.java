import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class B4Migration {
    private static final int BATCH_SIZE = 1000;

    private static String validate(String[] fields) {
        if (fields.length != 5) {
            return "Expected 5 fields but found " + fields.length;
        }

        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
            if (fields[i].isEmpty()) {
                return "Field " + (i + 1) + " is empty";
            }
        }

        try {
            if (Integer.parseInt(fields[3]) <= 0) {
                return "Route number must be positive";
            }
        } catch (NumberFormatException e) {
            return "Route number is not an integer";
        }

        try {
            if (new BigDecimal(fields[4]).signum() < 0) {
                return "Amount cannot be negative";
            }
        } catch (NumberFormatException e) {
            return "Amount is not a valid decimal number";
        }

        return null;
    }

    public static void main(String[] args) throws Exception {
        Path input = Path.of(
            "Task09-B4-Final-Engineering-Decision",
            "input", "buspasses.csv"
        );

        Path report = Path.of(
            "Task09-B4-Final-Engineering-Decision",
            "reports", "migration-failures.txt"
        );

        Files.createDirectories(report.getParent());

        String sql = "INSERT INTO bus_pass "
                   + "(pass_id, reg_no, name, route_no, amount) "
                   + "VALUES (?, ?, ?, ?, ?)";

        int total = 0;
        int valid = 0;
        int failed = 0;
        int pending = 0;

        try (BufferedReader reader =
                 Files.newBufferedReader(input, StandardCharsets.UTF_8);
             BufferedWriter reportWriter =
                 Files.newBufferedWriter(report, StandardCharsets.UTF_8);
             Connection con = B4Connection.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                reportWriter.write(
                    "B4 MIGRATION VALIDATION FAILURE REPORT"
                );
                reportWriter.newLine();
                reportWriter.write("Input file: " + input);
                reportWriter.newLine();
                reportWriter.newLine();

                String line;
                int lineNumber = 0;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    total++;

                    if (lineNumber == 1 && line.startsWith("\uFEFF")) {
                        line = line.substring(1);
                    }

                    if (line.trim().isEmpty()) {
                        failed++;
                        reportWriter.write(
                            "Line " + lineNumber + ": blank record"
                        );
                        reportWriter.newLine();
                        continue;
                    }

                    String[] fields = line.split(",", -1);
                    String problem = validate(fields);

                    if (problem != null) {
                        failed++;
                        reportWriter.write(
                            "Line " + lineNumber + ": "
                            + problem + " | " + line
                        );
                        reportWriter.newLine();
                        continue;
                    }

                    ps.setString(1, fields[0]);
                    ps.setString(2, fields[1]);
                    ps.setString(3, fields[2]);
                    ps.setInt(4, Integer.parseInt(fields[3]));
                    ps.setBigDecimal(5, new BigDecimal(fields[4]));
                    ps.addBatch();

                    valid++;
                    pending++;

                    if (pending == BATCH_SIZE) {
                        ps.executeBatch();
                        ps.clearBatch();
                        pending = 0;
                    }
                }

                if (pending > 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }

                con.commit();

            } catch (SQLException | RuntimeException | IOException e) {
                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                reportWriter.write(
                    "DATABASE OR MIGRATION FAILURE: " + e.getMessage()
                );
                reportWriter.newLine();
                reportWriter.write(
                    "The complete database transaction was rolled back."
                );
                reportWriter.newLine();

                throw e;
            }
        }

        System.out.println("\nB4 STREAMING MIGRATION SUMMARY");
        System.out.println("Total input records: " + total);
        System.out.println("Valid records imported: " + valid);
        System.out.println("Validation failures: " + failed);
        System.out.println("Batch size: " + BATCH_SIZE);
        System.out.println("Transaction: COMMITTED");
        System.out.println("Failure report: " + report);
        System.out.println("B4 streaming migration completed.");
    }
}
