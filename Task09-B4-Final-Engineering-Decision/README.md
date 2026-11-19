# B4 – Final Engineering Decision

## Environment
- Language: Java
- JDK: 21
- Database: MySQL 8.0
- JDBC driver: MySQL Connector/J 26.10.0
- Storage engine: InnoDB

## Files
- `src/B4DatabaseSetup.java` – creates and prepares the test database.
- `src/B4Connection.java` – creates JDBC connections.
- `src/B4Migration.java` – validates and migrates records using streaming batch insertion and one transaction.
- `src/B4Metadata.java` – inspects database and JDBC metadata.
- `database/schema.sql` – database table definition.
- `input/buspasses.csv` – migration input.
- `reports/migration-failures.txt` – invalid-record report.
- `B4-Analysis.md` – complexity analysis and final engineering decision.

## Compile
Run from the assignment root folder:

```powershell
$base = "Task09-B4-Final-Engineering-Decision"
$jar = "Task08-B3-JDBC-Batch/lib/mysql-connector-j-26.10.0.jar"

javac -cp $jar -d "$base/out" `
  "$base/src/B4DatabaseSetup.java" `
  "$base/src/B4Connection.java" `
  "$base/src/B4Migration.java" `
  "$base/src/B4Metadata.java"
```

## Run
Prepare the database:

```powershell
$cp = "$base/out;$jar"
java -cp $cp B4DatabaseSetup
```

Run the migration:

```powershell
java -cp $cp B4Migration
```

Inspect database metadata:

```powershell
java -cp $cp B4Metadata
```

Each program prompts for the MySQL password. Enter it locally; do not store it in source code or commit it.

## Tested migration result
- Total input records: 4
- Valid records imported: 3
- Validation failures: 1
- Batch size: 1,000
- Transaction: committed

The damaged record on line 2 was reported in `reports/migration-failures.txt`.

See `B4-Analysis.md` for the detailed analysis, benchmark evidence and final production recommendation.