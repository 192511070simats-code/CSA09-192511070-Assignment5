import java.math.BigDecimal;
import java.sql.*;
import java.util.Scanner;

public class WalletTransfer {
    private static final String URL =
        "jdbc:mysql://localhost:3306/assignment5_db";

    private static void showBalances(Connection con, String heading)
            throws SQLException {
        System.out.println("\n" + heading);
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT wallet_id, balance FROM wallets ORDER BY wallet_id")) {
            while (rs.next()) {
                System.out.println(rs.getString("wallet_id") + ": Rs. "
                    + rs.getBigDecimal("balance"));
            }
        }
    }

    public static void main(String[] args) {
        boolean simulateFailure =
            args.length > 0 && args[0].equalsIgnoreCase("fail");

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            try (Connection con =
                     DriverManager.getConnection(URL, "root", password)) {

                showBalances(con, "Balances before transfer:");

                con.setAutoCommit(false);

                try {
                    try (PreparedStatement debit = con.prepareStatement(
                        "UPDATE wallets SET balance = balance - ? " +
                        "WHERE wallet_id = 'HOSTEL' AND balance >= ?")) {
                        debit.setBigDecimal(1, new BigDecimal("500.00"));
                        debit.setBigDecimal(2, new BigDecimal("500.00"));

                        if (debit.executeUpdate() != 1) {
                            throw new SQLException(
                                "Hostel wallet has insufficient funds.");
                        }
                    }

                    System.out.println("Hostel wallet debit executed.");

                    if (simulateFailure) {
                        throw new SQLException(
                            "Simulated failure between the two updates.");
                    }

                    try (PreparedStatement credit = con.prepareStatement(
                        "UPDATE wallets SET balance = balance + ? " +
                        "WHERE wallet_id = 'CANTEEN'")) {
                        credit.setBigDecimal(1, new BigDecimal("500.00"));

                        if (credit.executeUpdate() != 1) {
                            throw new SQLException(
                                "Canteen wallet was not found.");
                        }
                    }

                    con.commit();
                    System.out.println("PASS: Transaction committed.");
                    System.out.println("Rs. 500.00 transferred successfully.");

                } catch (SQLException e) {
                    con.rollback();
                    System.out.println("ROLLBACK: " + e.getMessage());
                    System.out.println(
                        "Both wallet updates were cancelled.");
                } finally {
                    con.setAutoCommit(true);
                }

                showBalances(con, "Balances after transaction:");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
