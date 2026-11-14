# A1 – Design a File Format

## 1. Problem Description

The bus pass system stores student bus pass details. Each record contains seven fields:

1. Pass ID
2. Student Register Number
3. Student Name
4. Route
5. Boarding Stop
6. Validity Date
7. Amount

The system is expected to store approximately 8,000 bus pass records per year. The file design must support adding new passes, finding a pass by its ID, and listing passes belonging to a particular route.

## 2. Text File Format

A text file stores one bus pass record per line. Commas separate the seven fields.

Example:

```text
P1001,192511070,Suthesika,Route-A,Central Stop,2027-03-31,1500
P1002,192511071,Student2,Route-B,North Stop,2027-03-31,1800
P1003,192511072,Student3,Route-A,East Stop,2027-03-31,1500
```

The validity date uses the ISO format YYYY-MM-DD. The amount is stored as a whole number.

Advantages:
- Human-readable and easy to inspect.
- Simple to edit and transfer.
- Easy to process using BufferedReader and BufferedWriter.

Disadvantages:
- Every field must be converted from text to its required data type.
- Searching normally requires scanning the file.
- Delimiters inside field values require escaping or validation.

Design rule: commas and line breaks are not permitted inside individual fields unless a proper escaping format is implemented.

## 3. Binary File Format

A binary file stores field values using DataOutputStream. Each record is written in the following order:

| Field | Java write method |
|---|---|
| Pass ID | writeUTF() |
| Register Number | writeUTF() |
| Student Name | writeUTF() |
| Route | writeUTF() |
| Boarding Stop | writeUTF() |
| Validity Date | writeUTF() |
| Amount | writeInt() |

Example pseudocode:

```text
OPEN a DataOutputStream for the file
FOR each bus pass:
    writeUTF(passId)
    writeUTF(registerNumber)
    writeUTF(studentName)
    writeUTF(route)
    writeUTF(boardingStop)
    writeUTF(validityDate)
    writeInt(amount)
CLOSE the output stream
```

The reading program must read the fields in exactly the same order and using the matching methods.

Advantages:
- More compact and structured than ordinary text in many cases.
- Numeric values can be stored directly as numeric types.
- Suitable for programs that repeatedly read structured records.

Disadvantages:
- Not human-readable.
- Requires a matching reader and writer.
- Changes to the field order or data types can make old files incompatible.

## 4. File Operations – Pseudocode

### 4.1 Add a Bus Pass

```text
FUNCTION addPass(pass):
    VALIDATE all seven fields
    IF the pass ID already exists:
        RETURN duplicate-pass error

    OPEN file using BufferedWriter in append mode
    FORMAT the seven fields as one comma-separated line
    WRITE the line
    CLOSE the writer
    RETURN success
    IF an I/O error occurs:
        RETURN file-write error
```

### 4.2 Find a Bus Pass by Pass ID

```text
FUNCTION findPass(searchId):
    IF file does not exist:
        RETURN file-not-found error

    OPEN file using BufferedReader
    WHILE another line is available:
        READ one line
        SPLIT the line into seven fields

        IF the line is damaged:
            SKIP the line
            CONTINUE

        IF the first field equals searchId:
            CLOSE the reader
            RETURN the matching bus pass

    CLOSE the reader
    RETURN not-found result
```

### 4.3 List Passes by Route

```text
FUNCTION listByRoute(searchRoute):
    IF file does not exist:
        RETURN file-not-found error

    OPEN file using BufferedReader
    CREATE an empty result list

    WHILE another line is available:
        READ one line
        SPLIT the line into seven fields

        IF the line is damaged:
            SKIP the line
            CONTINUE

        IF the route field equals searchRoute:
            ADD the bus pass to the result list

    CLOSE the reader
    RETURN the result list
```

## 5. Error Handling and Reliability

| Problem | Recommended handling |
|---|---|
| Missing file | Check existence and report a clear error. |
| Damaged line | Validate the field count and data types; skip invalid records and continue. |
| Invalid amount | Catch number-format errors and reject the record. |
| Empty fields | Validate required fields before writing. |
| Incomplete write | Write the updated data to a temporary file, flush and close it, then replace the original file. Preserve the original if replacement fails. |

For a small append operation, an I/O failure may leave a partially written final line. The reader should detect and reject that damaged line. For a safer full-file update, a temporary-file-and-replacement strategy is preferable.

## 6. RandomAccessFile and Fixed-Length Records

An alternative is to store records with fixed-width fields using RandomAccessFile.

An example record layout is:

| Field | Fixed width in bytes |
|---|---:|
| Pass ID | 10 |
| Register Number | 12 |
| Student Name | 40 |
| Route | 20 |
| Boarding Stop | 40 |
| Validity Date | 10 |
| Amount | 8 |
| **Total** | **140 bytes** |

Each field is encoded using UTF-8, checked to ensure it fits its allocated byte width, and padded to the required width. The amount can instead be stored as a fixed-width numeric representation.

For a 140-byte record, the byte offset of a known record number is:

```text
offset = (recordNumber - 1) * 140
```

The program can use seek(offset) to move directly to that record.

### Sequential Scan vs Direct Seek

| Feature | Sequential scan | Direct seek |
|---|---|---|
| Access | Read records one after another | Jump to a known record position |
| Time | O(n) | O(1) to access a known record number |
| Search by pass ID | O(n) without an index | Still O(n) without an index |
| Storage | Variable-length records possible | Fixed-length records |
| Main limitation | Slower for large searches | Padding wastes storage |

Important: direct seeking is O(1) only when the record position is already known. Searching for an unknown pass ID still requires scanning all records unless an additional index is maintained.

### Padding Waste

Fixed-length records reserve the same space for every field. A short name may use only a few bytes while occupying a 40-byte field. This unused space is padding waste. UTF-8 fields must be measured in bytes, not Java characters.

## 7. Final Design Decision

For the initial bus pass system, the text format is recommended because it is readable, easy to debug and suitable for approximately 8,000 records per year. BufferedReader and BufferedWriter provide convenient line-based processing.

The binary format is suitable when structured binary storage is preferred. RandomAccessFile is useful when records have fixed lengths and their record positions are known. However, it requires careful field-width validation and wastes space through padding.

For a small file-based system, the text format provides the simplest maintainable solution. Performance will be measured in B1 before making a final implementation decision.
