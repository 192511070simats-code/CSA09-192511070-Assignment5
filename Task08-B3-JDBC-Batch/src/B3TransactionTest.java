import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class B3TransactionTest {

    private static final String INSERT =
        "INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount) VALUES(?,?,?,?,?)";

    private static List<String[]> makePasses() {
        List<String[]> passes = new ArrayList<>();

        for (int i = 1; i <= 2000; i++) {
            String amount = String.valueOf(2500.0 + i);

            if (i == 1001) {
                amount = "INVALID_AMOUNT";
            }

            passes.add(new String[] {
                String.format("T%05d", i),
                String.format("19232%05d", i),
                "Student" + i,
                String.valueOf((i % 20) + 1),
                amount
            });
        }

        return passes;
    }

    private static void clearTable(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("DELETE FROM bus_pass");
        }
    }

    private static int countRecords(Connection con) throws SQLException {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT COUNT(*) FROM bus_pass")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private static void checkEngine(Connection con) throws SQLException {
        String sql = "SELECT ENGINE FROM information_schema.TABLES "
                   + "WHERE TABLE_SCHEMA = 'b3_jdbc_test' "
                   + "AND TABLE_NAME = 'bus_pass'";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (!rs.next() || !"InnoDB".equalsIgnoreCase(rs.getString(1))) {
                throw new SQLException("bus_pass must use InnoDB.");
            }
            System.out.println("Storage engine: " + rs.getString(1));
        }
    }

    private static void testAutoCommit(Connection con, List<String[]> passes)
            throws SQLException {

        clearTable(con);
        con.setAutoCommit(true);

        System.out.println("\nTEST 1 - Batch with auto-commit ON");

        try (PreparedStatement ps = con.prepareStatement(INSERT)) {
            int count = 0;

            for (String[] p : passes) {
                ps.setString(1, p[0]);
                ps.setString(2, p[1]);
                ps.setString(3, p[2]);
                ps.setInt(4, Integer.parseInt(p[3]));
                ps.setString(5, p[4]);
                ps.addBatch();
                count++;

                if (count == 1000 || count == 1001) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }

            System.out.println("Unexpected: all records inserted.");
        } catch (SQLException e) {
            System.out.println("Expected database failure: " + e.getMessage());
        }

        int remaining = countRecords(con);
        System.out.println("Records remaining after failure: " + remaining);

        if (remaining != 1000) {
            throw new SQLException(
                "Auto-commit test expected 1000 saved records, found " + remaining);
        }

        System.out.println("PASS: First 1000 records remained committed.");
    }

    private static void testTransaction(Connection con, List<String[]> passes)
            throws SQLException {

        clearTable(con);
        System.out.println("\nTEST 2 - Batch inside one transaction");

        try {
            PassLoader.loadAll(con, passes);
            throw new SQLException("Expected invalid amount failure did not occur.");
        } catch (NumberFormatException e) {
            System.out.println("Expected invalid amount failure: " + e.getMessage());
        }

        int remaining = countRecords(con);
        System.out.println("Records remaining after rollback: " + remaining);

        if (remaining != 0) {
            throw new SQLException(
                "Transaction test expected 0 records, found " + remaining);
        }

        System.out.println("PASS: All records were rolled back.");
    }

    public static void main(String[] args) {
        try (Connection con = B3Connection.getConnection()) {
            checkEngine(con);

            List<String[]> passes = makePasses();

            testAutoCommit(con, passes);
            testTransaction(con, passes);

            System.out.println("\nB3.3 transaction tests completed.");

        } catch (SQLException e) {
            System.out.println("B3.3 ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
