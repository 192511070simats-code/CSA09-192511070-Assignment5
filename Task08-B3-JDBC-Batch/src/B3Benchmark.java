import java.sql.*;
import java.util.*;

public class B3Benchmark {

    private static final String INSERT =
        "INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount) VALUES(?,?,?,?,?)";

    private static List<String[]> generatePasses(int size) {
        List<String[]> passes = new ArrayList<>();

        for (int i = 1; i <= size; i++) {
            passes.add(new String[] {
                String.format("P%06d", i),
                "19231" + String.format("%06d", i),
                "Student" + i,
                String.valueOf((i % 20) + 1),
                String.valueOf(2500.0 + (i % 100) * 100)
            });
        }

        return passes;
    }

    private static void clearTable(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("DELETE FROM bus_pass");
        }
    }

    private static long insertStatement(
            Connection con, List<String[]> passes) throws SQLException {

        clearTable(con);
        long start = System.nanoTime();

        try (Statement st = con.createStatement()) {
            for (String[] p : passes) {
                String sql = "INSERT INTO bus_pass VALUES ('"
                    + p[0] + "', '"
                    + p[1] + "', '"
                    + p[2] + "', "
                    + p[3] + ", "
                    + p[4] + ")";

                st.executeUpdate(sql);
            }
        }

        return System.nanoTime() - start;
    }

    private static long insertPrepared(
            Connection con, List<String[]> passes) throws SQLException {

        clearTable(con);
        long start = System.nanoTime();

        try (PreparedStatement ps = con.prepareStatement(INSERT)) {
            for (String[] p : passes) {
                setParameters(ps, p);
                ps.executeUpdate();
            }
        }

        return System.nanoTime() - start;
    }

    private static long insertBatch(
            Connection con, List<String[]> passes) throws SQLException {

        clearTable(con);
        long start = System.nanoTime();

        try (PreparedStatement ps = con.prepareStatement(INSERT)) {
            int batchCount = 0;

            for (String[] p : passes) {
                setParameters(ps, p);
                ps.addBatch();
                batchCount++;

                if (batchCount == 1000) {
                    ps.executeBatch();
                    ps.clearBatch();
                    batchCount = 0;
                }
            }

            if (batchCount > 0) {
                ps.executeBatch();
                ps.clearBatch();
            }
        }

        return System.nanoTime() - start;
    }

    private static void setParameters(
            PreparedStatement ps, String[] p) throws SQLException {

        ps.setString(1, p[0]);
        ps.setString(2, p[1]);
        ps.setString(3, p[2]);
        ps.setInt(4, Integer.parseInt(p[3]));
        ps.setDouble(5, Double.parseDouble(p[4]));
    }

    private static int countRows(Connection con) throws SQLException {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT COUNT(*) FROM bus_pass")) {

            rs.next();
            return rs.getInt(1);
        }
    }

    private static double milliseconds(long nanoseconds) {
        return nanoseconds / 1_000_000.0;
    }

    public static void main(String[] args) {
        int[] sizes = {1000, 10000, 100000};

        try (Connection con = B3Connection.getConnection()) {
            con.setAutoCommit(true);

            System.out.println("\nB3.2 JDBC INSERT PERFORMANCE");
            System.out.println("Auto-commit: ON");
            System.out.println("Batch size: 1000");
            System.out.println("Time unit: milliseconds\n");

            System.out.println(
                "Records,Statement,PreparedStatement,BatchPreparedStatement");

            for (int size : sizes) {
                List<String[]> passes = generatePasses(size);

                long statementTime = insertStatement(con, passes);
                int statementCount = countRows(con);

                long preparedTime = insertPrepared(con, passes);
                int preparedCount = countRows(con);

                long batchTime = insertBatch(con, passes);
                int batchCount = countRows(con);

                if (statementCount != size ||
                    preparedCount != size ||
                    batchCount != size) {
                    throw new SQLException(
                        "Record count verification failed for " + size);
                }

                System.out.printf(
                    Locale.US, "%d,%.3f,%.3f,%.3f%n",
                    size,
                    milliseconds(statementTime),
                    milliseconds(preparedTime),
                    milliseconds(batchTime)
                );

                System.out.println(
                    "PASS: All three methods inserted and verified "
                    + size + " records.");
            }

            System.out.println("\nB3.2 benchmark completed.");

        } catch (SQLException e) {
            System.out.println("B3.2 ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
