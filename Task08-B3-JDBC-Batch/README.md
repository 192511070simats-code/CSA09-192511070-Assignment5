# B3 – JDBC Statement, PreparedStatement and Batch

**Assignment:** Programming in Java – Assignment 5
**Java Version:** JDK 21
**Database:** MySQL 8

## Objective
Compare Statement, PreparedStatement and batch insertion. Test correctness, performance and transaction safety.

## Project Files
- src/B3Connection.java – Database connection.
- src/PassLoader.java – Transaction-safe batch loader.
- src/B3SmallDataTest.java – Correctness tests.
- src/B3Benchmark.java – Performance tests.
- src/B3TransactionTest.java – Transaction tests.
- database/schema.sql – Database definition.
- B3-Analysis.md – Detailed analysis.

## Database
Database: b3_jdbc_test
Table: bus_pass
Storage engine: InnoDB

## Actual Performance Results

| Records | Statement (ms) | PreparedStatement (ms) | Batch (ms) |
|---:|---:|---:|---:|
| 1,000 | 2849.955 | 2598.769 | 87.716 |
| 10,000 | 23138.627 | 22626.527 | 358.491 |
| 100,000 | 219496.902 | 233232.550 | 3242.499 |

## Correctness
All three insertion methods successfully inserted and verified 10 records. ResultSetMetaData confirmed the five column types.

## Transaction Results
- Auto-commit ON: 1,000 records remained after failure.
- Single transaction: 0 records remained after rollback.

## Conclusion
PreparedStatement batch insertion was the fastest approach in the benchmark. PreparedStatement with executeUpdate() is suitable for a single record. Transaction rollback provides all-or-nothing behaviour with InnoDB.

See B3-Analysis.md for the detailed engineering trade-off analysis.