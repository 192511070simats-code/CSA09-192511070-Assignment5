# B4 – Final Engineering Decision

## B4.1 – File-to-Database Migration

The migration program reads the existing bus-pass CSV file using BufferedReader. It validates each record before adding it to a PreparedStatement batch.

Validation checks include:
- Exactly five fields per record.
- No blank fields.
- A positive integer route number.
- A valid, non-negative decimal amount.

Invalid records are written to `reports/migration-failures.txt`, including their line numbers and reasons.

Valid records are inserted in batches of 1,000. The program disables auto-commit and commits the complete migration as one transaction. If a database or migration error occurs, it attempts to roll back the transaction.

### Migration test evidence

Input file: 4 records.

| Result | Count |
|---|---:|
| Total input records | 4 |
| Valid records imported | 3 |
| Validation failures | 1 |
| Batch size | 1,000 |

The damaged second line was:

`P1002,DAMAGEDLINE`

The failure report identified it as having two fields instead of the required five.

The migration completed successfully and committed the three valid records.

### Migration complexity

Let N be the number of input records and B be the batch size.

- Reading and validation: O(N) time.
- Batch insertion: O(N) database insert work, grouped into approximately O(N/B) batch executions.
- Additional application memory: O(B), because only the current batch is retained.
- Failure reporting: O(F) output space, where F is the amount of failure information written to the report.

The streaming approach avoids retaining all valid input records in memory.

## B4.2 – Database Metadata and Portability

The program uses Java DatabaseMetaData to inspect the database at runtime.

### Observed metadata

| Property | Observed value |
|---|---|
| Database product | MySQL |
| Database version | 8.0.44 |
| JDBC driver | MySQL Connector/J |
| Driver version | mysql-connector-j-26.10.0 |
| Table found | bus_pass |

DatabaseMetaData helps an application identify the database vendor, version, JDBC driver and available tables. This information is useful when deploying an application to another server because it helps identify configuration and compatibility differences.

Metadata inspection improves portability, but does not guarantee it. SQL syntax, database-specific data types, schema definitions, connection settings and authentication may still need adjustment when changing database vendors.

## B4.3 – Final Production Decision

### Recommended architecture: Shared relational database

For a system serving 12 campuses with 100 times the current number of bus passes and concurrent counter operations, a shared MySQL database using InnoDB is recommended.

Reasons:
1. Indexed searches can locate a pass without scanning every record.
2. Transactions protect related changes from partial completion.
3. InnoDB provides concurrency controls and row-level locking for data operations.
4. A central database makes data management, backup and reporting easier across campuses.
5. PreparedStatement parameter binding reduces SQL injection risk.
6. Batch insertion improves the efficiency of large imports.

### Comparison of storage approaches

| Criterion | Text files | Java serialization | Shared database |
|---|---|---|---|
| Search | Sequential scan O(N) | Sequential scan O(N) without an index | Indexed lookup typically O(log N) |
| Storage | O(N) | O(N), plus serialization overhead | O(N), plus indexes |
| Concurrent updates | Requires file coordination | Requires file locking and coordination | Database transaction and locking support |
| Data integrity | Must be implemented by application | Must be implemented by application | Constraints and transactions |
| Portability | Broad text compatibility | Java-specific object format | JDBC supports multiple database vendors, with SQL differences |
| Multi-campus management | Separate files need coordination | Separate object files need coordination | Centralized management |

Here, N represents the total number of pass records. An unindexed database search can also take O(N); indexed lookup performance depends on the index and database execution plan.

### Alternative: One serialized file per campus

If each of the 12 campuses maintains a separate serialized file:

- Total storage remains O(N), excluding serialization overhead.
- Searching one campus file takes O(n_i) without an additional index.
- Searching all 12 files sequentially takes O(n_1 + n_2 + ... + n_12), which is O(N).
- A central registry or directory is needed to map campus IDs to their files.
- Different campuses can work independently, but simultaneous updates to the same file require file locking.
- Global transactions, duplicate-ID checks across campuses and synchronized backups become more difficult.

A separate index could improve search performance, but it would need to be updated consistently with the campus files.

### Evidence from earlier tasks

The B1 file-search measurements showed increasing average search time as the number of records increased:

| Records | Average last-record search (ms) |
|---:|---:|
| 1,000 | 4.1312 |
| 10,000 | 18.4782 |
| 50,000 | 35.3090 |
| 100,000 | 62.6439 |

The B3 JDBC batch benchmark produced these measurements:

| Records | Statement (ms) | PreparedStatement (ms) | Batch (ms) |
|---:|---:|---:|---:|
| 1,000 | 2,849.955 | 2,598.769 | 87.716 |
| 10,000 | 23,138.627 | 22,626.527 | 358.491 |
| 100,000 | 219,496.902 | 233,232.550 | 3,242.499 |

These are measurements from the test environment, not guaranteed timings for production hardware. They demonstrate the benefit of batch insertion in the tested workload.

The B3 transaction test also demonstrated that auto-commit could leave earlier records inserted after a later failure, whereas a single transaction rolled back the complete import.

### Final conclusion

A shared MySQL/InnoDB database is the most suitable choice for the proposed 12-campus system. It provides a stronger foundation for indexed searches, concurrent counter operations, transaction safety, centralized administration and large-scale batch imports. Text files and serialization remain useful for simple exports, backups or small standalone applications, but are not the preferred primary storage system for this requirement.