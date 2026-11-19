# B3 – JDBC Statement, PreparedStatement and Batch Performance

## B3.1 Correctness on Small Data

Three JDBC insertion approaches were tested using 10 bus-pass records:

1. Statement – SQL INSERT statements were constructed using string concatenation.
2. PreparedStatement – parameters were bound using setString(), setInt() and setDouble(), then each row was inserted using executeUpdate().
3. PreparedStatement Batch – parameter values were added using addBatch() and inserted using executeBatch().

After each approach, a SELECT query retrieved the table contents. ResultSetMetaData verified the five column names and database types.

Verified column types:

| Column | MySQL type |
|---|---|
| pass_id | VARCHAR |
| reg_no | VARCHAR |
| name | VARCHAR |
| route_no | INT |
| amount | DECIMAL |

All three approaches inserted and verified exactly 10 records.

## B3.2 Performance with Increasing Data Size

The three approaches were benchmarked with 1,000, 10,000 and 100,000 records.

All three approaches used auto-commit ON. The batch approach used a batch size of 1,000. The time measurements include database insertion and verification was performed after each insertion.

### Actual benchmark results

| Records | Statement (ms) | PreparedStatement (ms) | Batch PreparedStatement (ms) |
|---:|---:|---:|---:|
| 1,000 | 2849.955 | 2598.769 | 87.716 |
| 10,000 | 23138.627 | 22626.527 | 358.491 |
| 100,000 | 219496.902 | 233232.550 | 3242.499 |

### Observations

- Statement and PreparedStatement execute individual INSERT operations.
- PreparedStatement supports parameter binding and separates SQL structure from data.
- Batch PreparedStatement groups multiple insert operations and executes them together.
- Batch insertion was considerably faster for all three tested data sizes.
- At 100,000 records, batch insertion took approximately 3.24 seconds, while Statement took approximately 219.50 seconds and PreparedStatement took approximately 233.23 seconds.
- The measured results are specific to this computer, database configuration and JDBC driver.

### Performance comparison

For 100,000 records:

- Statement was approximately 67.7 times slower than batch insertion.
- PreparedStatement was approximately 71.9 times slower than batch insertion.

These are measured ratios, not universal performance guarantees.

## B3.3 Auto-commit vs Transaction

A test containing 2,000 records was created. Record 1,001 contained an invalid amount to force an insertion failure.

The MySQL table used the InnoDB storage engine.

### Test 1 – Batch with auto-commit ON

- The first 1,000 records were executed as the first batch.
- Because auto-commit was enabled, the first batch was committed.
- The next batch contained the invalid amount.
- MySQL rejected the invalid DECIMAL value.
- The first 1,000 records remained in the table.

Result: 1,000 records remained after failure.

### Test 2 – Batch inside one transaction

- Auto-commit was disabled.
- The first 1,000 records were inserted but not committed.
- The invalid amount at record 1,001 caused a NumberFormatException.
- The error handling code executed rollback().
- No inserted records remained.

Result: 0 records remained after rollback.

### Comparison

| Feature | Auto-commit ON | Single transaction |
|---|---|---|
| First batch | Committed | Not committed |
| Failure handling | Earlier committed rows remain | All transaction changes rolled back |
| Remaining records | 1,000 | 0 |
| Suitable for atomic import | No | Yes |

The transaction approach provides all-or-nothing behaviour when the database tables use a transactional storage engine such as InnoDB.

## B3.4 Engineering Trade-offs

### Recommended approach for a nightly import of 100,000 records

PreparedStatement batch insertion is recommended.

Reasons:

- The measured execution time was approximately 3.24 seconds.
- It was substantially faster than individual Statement and PreparedStatement execution.
- Parameter binding provides safer handling of input values.
- A batch size of 1,000 limits the number of pending JDBC batch entries.
- Using one transaction provides atomicity when the import must either succeed completely or be rolled back.

The transaction should be committed only after successful completion. On failure, rollback should be performed.

### Recommended approach for a single counter pass

For inserting only one counter pass, PreparedStatement with executeUpdate() is appropriate.

Reasons:

- Only one record needs to be inserted.
- Batch setup and execution are unnecessary.
- Parameter binding is safer than constructing SQL through string concatenation.
- The code is simple and easy to maintain.

### Alternative 1 – Multi-row INSERT

A multi-row INSERT can insert several records in one SQL statement.

Example:

INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount)
VALUES
('P001', '192310001', 'Arun', 12, 4500.00),
('P002', '192310002', 'Divya', 7, 3800.00);

For n records and k records per SQL statement:

- Number of INSERT statements: approximately ceil(n/k).
- Processing complexity: O(n).
- Application memory for a generated statement: O(k).

It can reduce database communication overhead, but statement size is limited by MySQL packet and SQL-size constraints. Parameterized multi-row INSERT statements are preferable to unsafe concatenation.

### Alternative 2 – MySQL LOAD DATA

MySQL LOAD DATA can import records directly from a file.

Advantages:

- Designed for bulk data loading.
- Reduces repeated individual INSERT operations.
- Can be efficient for very large files.

Disadvantages:

- Requires file access and suitable MySQL permissions.
- Input formatting and validation must be handled carefully.
- Application-specific validation and error reporting may require additional processing.

For very large nightly file imports, LOAD DATA should be benchmarked against JDBC batch insertion before selecting the final implementation.

### Time and Memory Complexity

Let:

- n = total number of records.
- b = JDBC batch size.
- k = number of records in a multi-row INSERT.

| Approach | Database submissions | Processing complexity | Additional application memory |
|---|---:|---|---|
| Statement, one row at a time | n | O(n) | O(1) per row |
| PreparedStatement, one row at a time | n | O(n) | O(1) per row |
| PreparedStatement batch | Approximately ceil(n/b) | O(n) | O(b) for pending batch entries |
| Multi-row INSERT | Approximately ceil(n/k) | O(n) | O(k) for generated statement |
| LOAD DATA | Bulk import command | O(n) processing | Can stream input with low application memory |

The supplied PassLoader receives a complete List<String[]> containing all records. Therefore, its input list itself requires O(n) memory, in addition to O(b) memory for the pending JDBC batch.

### Batch Size Trade-off

A larger batch can reduce the number of database submissions, but it also increases memory use and the size of pending operations. A smaller batch uses less memory but may require more submissions.

A batch size of 1,000 was used in this experiment. Other batch sizes should be tested with the actual production data and database configuration.

## Conclusion

The experiment demonstrated that PreparedStatement batch insertion is the most suitable of the three tested JDBC approaches for the 100,000-record import. It achieved approximately 3.24 seconds in the local benchmark.

PreparedStatement with executeUpdate() is more appropriate for inserting a single counter pass.

The transaction test also demonstrated why auto-commit should be disabled for an import that requires all-or-nothing behaviour. InnoDB rollback removed all uncommitted records after the forced failure.

Multi-row INSERT and MySQL LOAD DATA are useful alternatives for bulk imports and should be evaluated using the actual production environment.
