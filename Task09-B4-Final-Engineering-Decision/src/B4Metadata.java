import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class B4Metadata {
    public static void main(String[] args) throws Exception {
        try (Connection con = B4Connection.getConnection()) {
            DatabaseMetaData metadata = con.getMetaData();

            System.out.println("\nDATABASE METADATA");
            System.out.println("Database product: "
                               + metadata.getDatabaseProductName());
            System.out.println("Database version: "
                               + metadata.getDatabaseProductVersion());
            System.out.println("JDBC driver: "
                               + metadata.getDriverName());
            System.out.println("Driver version: "
                               + metadata.getDriverVersion());

            System.out.println("\nTables in current database:");

            try (ResultSet tables = metadata.getTables(
                    con.getCatalog(), null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    System.out.println("- " + tables.getString("TABLE_NAME"));
                }
            }
        }
    }
}
