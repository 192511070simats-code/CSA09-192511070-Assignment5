# Programming in Java Assignment 5
## CO5: Data Persistence Engineering Files and Databases

**Name:** K Suthesika  
**Register Number:** 192511070  
**Java Version:** JDK 21

## Assignment Tasks

| Folder | Task |
|---|---|
| Task01 | A1  Design a File Format |
| Task02 | A2  Buffered I/O |
| Task03 | A3  Data-Access Design Pattern |
| Task04 | A4  Serialization |
| Task05 | A5  Transactions and Injection |
| Task06 | B1  Implement and Instrument the File Store |
| Task07 | B2  Adversarial Input |
| Task08 | B3  Statement vs PreparedStatement vs Batch |
| Task09 | B4  Final Engineering Decision |

## Compile and Run
Compile and run each Java program from its task folder. The exact commands and results will be added as each task is completed.

## Results
Task results, test evidence and actual performance measurements will be documented as the work progresses.

## A3  Data-Access Object (DAO) Pattern

**Folder:** Task03-A3-DAO-Pattern

**Implementation:**
- Created generic Dao<T, K> interface with five CRUD methods.
- Created Student model class.
- Implemented StudentDao using JDBC and PreparedStatement.
- Created the college_db student table using schema.sql.
- Used try-with-resources to close statements and result sets.

**Tests:**
- DaoDesignTest: passed.
- MySQL JDBC integration test: all five operations passed (insert, findById, findAll, update and delete).

**Database:** MySQL Server 8.0, database college_db, table student.


## A4 - What Should Be Serialized?

**Folder:** `Task04-A4-Serialization`

**Implementation:**
- Created serializable Hostel, Room and Resident classes.
- Marked Resident.password and Resident.databaseConnection as transient.
- Saved and restored the Hostel object graph using ObjectOutputStream and ObjectInputStream.
- Tested class evolution by adding phoneNumber while retaining serialVersionUID = 1L.

**Main test result:**
- 2 rooms and 3 residents restored successfully.
- Password and database connection fields were null after loading.
- Serialized file size: 445 bytes.

**Versioning test result:**
- Old serialized file loaded successfully using the new class.
- The new phoneNumber field received its default value, null.

**Analysis:** See `Task04-A4-Serialization/A4-Analysis.md`.

## A5 Design for Correctness and Safety

- Implemented JDBC wallet transfer using `setAutoCommit(false)`, `commit()` and `rollback()`.
- Tested a simulated failure between debit and credit; both balances remained unchanged after rollback.
- Tested successful transfer of Rs. 500; hostel balance became Rs. 500 and canteen balance became Rs. 700.
- Demonstrated SQL injection against string-concatenated login SQL.
- Redesigned login verification using parameterized `PreparedStatement`.
- Documented auto-commit failure risks, transaction overhead and time complexity.
- Analysis: `Task05-A5-Transaction-Safety/A5-Analysis.md`
- Java source: `Task05-A5-Transaction-Safety/src/`
- SQL scripts: `Task05-A5-Transaction-Safety/database/`

## B1 Implement and Instrument the File Store

**Folder:** `Task06-B1-File-Store`

**Implementation:**
- Implemented a text-based bus-pass file store with add, find and list operations.
- Used BufferedReader to search records line by line.
- Skipped damaged records that do not contain exactly five fields.
- Used try-with-resources for file handling.

**Validation:**
- Normal search, missing ID, damaged record, listing and adding tests passed.
- Search time measured for 1,000, 10,000, 50,000 and 100,000 records.

**Performance:**
- Compared buffered and unbuffered line counting for 1 MB, 10 MB and 50 MB files.
- Buffered reading was significantly faster in all three tests.
- Documented the HashMap alternative and its time and memory trade-offs.

**Analysis:** `Task06-B1-File-Store/B1-Analysis.md`
## B2 - Engineer an Adversarial Input

- Demonstrated SQL injection using a deliberately crafted username.
- Compared unsafe SQL concatenation with PreparedStatement.
- Tested damaged bus-pass records, including incorrect field count, non-numeric amount, empty line, empty name, and non-numeric route.
- Verified that stronger validation accepts two valid records and rejects five damaged or empty records.
- Analysis: Task07-B2-AdversarialInput/B2-Analysis.md
- Task instructions: Task07-B2-AdversarialInput/README.md

## B3 JDBC Statement, PreparedStatement and Batch

- Compared Statement, PreparedStatement and batch insertion.
- Verified 10 records using SELECT and ResultSetMetaData.
- Benchmarked 1,000, 10,000 and 100,000 records.
- At 100,000 records, batch insertion took 3,242.499 ms.
- Tested auto-commit and transaction rollback.
- Verified that auto-commit retained 1,000 records after failure, while transaction rollback retained zero.
- Compared batch insertion with alternative bulk-loading approaches.
- Detailed analysis: [B3-Analysis.md](Task08-B3-JDBC-Batch/B3-Analysis.md)
- Task instructions and results: [B3 README](Task08-B3-JDBC-Batch/README.md)

## B4 – Final Engineering Decision

- Implemented a streaming CSV-to-MySQL migration using BufferedReader and PreparedStatement batches of 1,000.
- Used one database transaction with commit and rollback handling.
- Validated records and generated a failure report with line numbers and reasons.
- Migration test: 4 input records, 3 valid records imported, 1 damaged record reported.
- Used DatabaseMetaData to identify MySQL 8.0.44, MySQL Connector/J 26.10.0 and the bus_pass table.
- Recommended a shared MySQL/InnoDB database for the 12-campus production system.
- Compared text files, Java serialization and relational database storage.
- Detailed analysis: Task09-B4-Final-Engineering-Decision/B4-Analysis.md
- Task instructions and commands: Task09-B4-Final-Engineering-Decision/README.md
