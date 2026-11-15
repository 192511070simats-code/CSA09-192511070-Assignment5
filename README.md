# Programming in Java – Assignment 5
## CO5: Data Persistence Engineering – Files and Databases

**Name:** K Suthesika  
**Register Number:** 192511070  
**Java Version:** JDK 21

## Assignment Tasks

| Folder | Task |
|---|---|
| Task01 | A1 – Design a File Format |
| Task02 | A2 – Buffered I/O |
| Task03 | A3 – Data-Access Design Pattern |
| Task04 | A4 – Serialization |
| Task05 | A5 – Transactions and Injection |
| Task06 | B1 – Implement and Instrument the File Store |
| Task07 | B2 – Adversarial Input |
| Task08 | B3 – Statement vs PreparedStatement vs Batch |
| Task09 | B4 – Final Engineering Decision |

## Compile and Run
Compile and run each Java program from its task folder. The exact commands and results will be added as each task is completed.

## Results
Task results, test evidence and actual performance measurements will be documented as the work progresses.

## A3 – Data-Access Object (DAO) Pattern

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
