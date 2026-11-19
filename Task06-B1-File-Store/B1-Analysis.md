# B1 – Implement and Instrument the File Store

## B1.1 – File Store Validation

The PassStore implementation supports adding, finding and listing bus-pass records using a comma-separated text file.

| Test case | Input / Action | Expected result | Actual result |
|---|---|---|---|
| Normal search | Find P1003 | Return Divya's record | PASS |
| Missing ID | Find P9999 | Return null | PASS |
| Damaged record | Find P1002 | Skip damaged record and return null | PASS |
| List records | List all records | Display valid records only | PASS |
| Add record | Add and find P1004 | Return newly added record | PASS |

The damaged P1002 line contains only two fields instead of five. The condition `fields.length != 5` identifies this invalid record and skips it using `continue`. The search continues without stopping because of the damaged line.

## B1.2 – Comparison with A1 Pseudocode

The implementation follows the text-file design from A1.

- The add operation appends a new comma-separated record.
- The find operation reads records sequentially until the requested pass ID is found.
- The list operation reads and displays all valid records.
- BufferedWriter and BufferedReader are used for efficient file access.
- Try-with-resources automatically closes the file resources.
- Invalid records that do not contain exactly five fields are skipped.

The implementation uses Java NIO methods such as Files.newBufferedReader() and Files.newBufferedWriter() instead of explicitly creating FileReader and FileWriter objects. These methods provide buffered character-based file access.

The simple comma-separated format assumes that field values, especially names, do not contain commas. A full CSV parser or escaping mechanism would be needed to support commas and quoted fields safely.

## B1.3 – Buffered and Unbuffered Line Counting

The following measurements were collected using the same input files.

| File size | Method | Lines | Successful read calls | Time (ms) |
|---|---|---:|---:|---:|
| 1 MB | Unbuffered | 18079 | 1048576 | 4470.162 |
| 1 MB | Buffered | 18079 | 128 | 23.943 |
| 10 MB | Unbuffered | 180789 | 10485760 | 39563.142 |
| 10 MB | Buffered | 180789 | 1280 | 62.342 |
| 50 MB | Unbuffered | 903945 | 52428800 | 200194.309 |
| 50 MB | Buffered | 903945 | 6400 | 163.119 |

The buffered method used an 8192-byte buffer.

Buffered reading was approximately 187 times faster for 1 MB, 635 times faster for 10 MB and 1227 times faster for 50 MB in these test runs.

Both methods produced identical line counts. This supports the A2 prediction that buffering reduces the overhead of repeated small read operations and improves performance.

The successful read-call counts represent Java-level read operations, not operating-system disk calls. Execution times may vary depending on hardware, caching and system activity.

## B1.4 – Search Time as File Size Grows

The growth test searches for the last record in each file, requiring a full sequential scan.

| Number of records | Search ID | Average search time (ms) |
|---:|---|---:|
| 1000 | P001000 | 4.1312 |
| 10000 | P010000 | 18.4782 |
| 50000 | P050000 | 35.3090 |
| 100000 | P100000 | 62.6439 |

The search time generally increases as the number of records grows because the program reads records sequentially until it finds the requested ID.

The worst-case time complexity of a file-based search is O(n), where n is the number of records. These measurements show the general increasing trend, although actual timings are affected by JVM warm-up, caching and system activity.

## Alternative Design – HashMap

An alternative is to load all records into a HashMap using the pass ID as the key.

| Approach | Search complexity | Memory usage |
|---|---|---|
| Sequential file search | O(n) per search | Low additional memory |
| HashMap lookup | O(1) average per lookup after loading | O(n) |

For q searches, sequential file searching can require O(q × n) time in the worst case. Building a HashMap requires O(n) average time and O(n) memory, followed by O(1) average lookup time per search. Therefore, the total average time becomes O(n + q).

A HashMap is suitable when many searches are required, but it consumes more memory and must be kept consistent with the file whenever records are added or changed.

## Conclusion

The bus-pass file store successfully supports adding, finding and listing records. Damaged records are skipped safely during reading. The performance experiment confirms that buffered reading is significantly faster than unbuffered reading. The search-growth experiment demonstrates the limitations of sequential file searching and explains why a HashMap may be useful for frequent lookups.