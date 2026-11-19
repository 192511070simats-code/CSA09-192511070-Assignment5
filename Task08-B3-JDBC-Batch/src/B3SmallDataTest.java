import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class B3SmallDataTest {

    private static final String INSERT =
        "INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount) VALUES(?,?,?,?,?)";

    private static void clearTable(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("DELETE FROM bus_pass");
        }
    }

    private static String[] makePass(int i) {
        return new String[] {
            String.format("P%04d", i),
            String.format("19231%04d", i),
            "Student" + i,
            String.valueOf((i % 20) + 1),
            String.valueOf(2500.0 + i * 100)
        };
    }

    private static void insertUsingStatement(Connection con)
            throws SQLException {
        clearTable(con);

        try (Statement st = con.createStatement()) {
            for (int i = 1; i <= 10; i++) {
                String[] p = makePass(i);

                String sql = "INSERT INTO bus_pass VALUES ('"
                    + p[0] + "', '"
                    + p[1] + "', '"
                    + p[2] + "', "
                    + p[3] + ", "
                    + p[4] + ")";

                st.executeUpdate(sql);
            }
        }

        System.out.println("\nSTATEMENT: inserted 10 records.");
        verify(con);
    }

    private static void insertUsingPreparedStatement(Connection con)
            throws SQLException {
        clearTable(con);

        try (PreparedStatement ps = con.prepareStatement(INSERT)) {
            for (int i = 1; i <= 10; i++) {
                String[] p = makePass(i);
                setParameters(ps, p);
                ps.executeUpdate();
            }
        }

        System.out.println("\nPREPARED STATEMENT: inserted 10 records.");
        verify(con);
    }

    private static void insertUsingBatch(Connection con)
            throws SQLException {
        clearTable(con);

        try (PreparedStatement ps = con.prepareStatement(INSERT)) {
            for (int i = 1; i <= 10; i++) {
                String[] p = makePass(i);
                setParameters(ps, p);
                ps.addBatch();
            }
            ps.executeBatch();
            ps.clearBatch();
        }

        System.out.println("\nPREPARED STATEMENT BATCH: inserted 10 records.");
        verify(con);
    }

    private static void setParameters(PreparedStatement ps, String[] p)
            throws SQLException {
        ps.setString(1, p[0]);
        ps.setString(2, p[1]);
        ps.setString(3, p[2]);
        ps.setInt(4, Integer.parseInt(p[3]));
        ps.setDouble(5, Double.parseDouble(p[4]));
    }

    private static void verify(Connection con) throws SQLException {
        String sql = "SELECT pass_id, reg_no, name, route_no, amount "
                   + "FROM bus_pass ORDER BY pass_id";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            ResultSetMetaData md = rs.getMetaData();

            System.out.println("Column metadata:");
            for (int i = 1; i <= md.getColumnCount(); i++) {
                System.out.println(
                    md.getColumnName(i) + " : "
                    + md.getColumnTypeName(i)
                );
            }

            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println(
                    rs.getString("pass_id") + " | "
                    + rs.getString("reg_no") + " | "
                    + rs.getString("name") + " | "
                    + rs.getInt("route_no") + " | "
                    + rs.getBigDecimal("amount")
                );
            }

            System.out.println("Verified record count: " + count);

            if (count != 10 || md.getColumnCount() != 5) {
                throw new SQLException("Verification failed.");
            }

            System.out.println("PASS: 10 records and 5 columns verified.");
        }
    }

    public static void main(String[] args) {
        try (Connection con = B3Connection.getConnection()) {
            insertUsingStatement(con);
            insertUsingPreparedStatement(con);
            insertUsingBatch(con);

            System.out.println("\nB3.1 correctness tests completed.");

        } catch (SQLException e) {
            System.out.println("B3.1 ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
