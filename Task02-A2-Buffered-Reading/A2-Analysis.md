
# A2 – Buffered Reading Performance Analysis

## 1. Problem Understanding

The problem involves reading a 50 MB attendance log file and counting the number of lines. The original approach uses FileInputStream.read() to read one byte at a time.

Reading a large file one byte at a time creates a very large number of read method calls. This can reduce performance.

The objective is to compare three approaches:

1. Unbuffered reading using FileInputStream.
2. Buffered reading using BufferedInputStream.
3. Reading all lines using Files.readAllLines().

## 2. A2.1 – Performance of Unbuffered Reading

When FileInputStream.read() is used without an additional buffer, each successful call reads one byte.

For a 50 MB file:

File size = 50 × 1024 × 1024
          = 52,428,800 bytes

Therefore:

Number of successful read calls = 52,428,800

This creates a large number of Java read method calls. Repeated calls increase processing overhead and make the program slower.

### Experimental Results

| File Size | Successful Read Calls | Execution Time |
|---|---:|---:|
| 1 MB | 1,048,576 | 5195.669 ms |
| 10 MB | 10,485,760 | 44964.562 ms |
| 50 MB | 52,428,800 | 228259.634 ms |

The results show that execution time increases significantly as file size increases.

## 3. A2.2 – Improved Buffered Reading Strategy

The performance can be improved by using BufferedInputStream with an 8 KB buffer.

The program reads a block of bytes into a byte array instead of reading one byte per method call.

Buffer size = 8192 bytes

For a 50 MB file:

Number of buffer read calls
= 52,428,800 / 8192
= 6,400 calls

This reduces the number of successful Java read calls from 52,428,800 to 6,400.

The buffer reduces repeated read operations and improves performance.

### Buffered Reading Results

| File Size | Buffer Size | Successful Buffer Read Calls | Execution Time |
|---|---:|---:|---:|
| 1 MB | 8192 bytes | 128 | 24.269 ms |
| 10 MB | 8192 bytes | 1280 | 61.381 ms |
| 50 MB | 8192 bytes | 6400 | 213.190 ms |

### Why Buffering Is Faster

- Data is read in blocks instead of one byte at a time.
- Fewer Java read method calls are required.
- Repeated input operations are reduced.
- The program uses a small, fixed-size buffer.
- It does not need to load the entire file into memory.

## 4. A2.3 – When Buffering Gives Little Benefit

Buffering may provide little or no noticeable performance improvement in some situations.

Examples:

1. When reading a very small file only once, the overhead of setting up a buffer may not provide a noticeable benefit.
2. When the data is already buffered by another layer, adding another buffer may provide little improvement.
3. When most execution time is spent processing data rather than reading it, buffering may not significantly reduce total execution time.

Therefore, buffering is especially useful when reading large files or performing repeated input operations.

## 5. Comparison with Files.readAllLines()

The Files.readAllLines() method reads all lines from a file and stores them in a List<String>.

### Experimental Results

| File Size | Number of Lines | Execution Time |
|---|---:|---:|
| 1 MB | 18,079 | 67.813 ms |
| 10 MB | 180,789 | 178.898 ms |
| 50 MB | 903,945 | 427.879 ms |

### Time and Space Complexity

| Method | Time Complexity | Extra Space Complexity |
|---|---|---|
| Unbuffered reading | O(n) | O(1) |
| Buffered reading | O(n) | O(B) |
| Files.readAllLines() | O(n) | O(n) |

Here:
- n represents the input file size.
- B represents the buffer size (8192 bytes).

Files.readAllLines() uses more memory because it stores all lines in a list. Buffered reading uses a fixed-size buffer and processes the file in blocks.

### Is Files.readAllLines() Suitable for a 50 MB File?

It can work for a 50 MB file if sufficient memory is available. However, the actual memory required can be much larger than 50 MB because Java also stores String objects and list entries.

For very large attendance logs, buffered reading is more memory-efficient because it processes the file without storing every line.

## 6. Overall Performance Comparison

| File Size | Unbuffered Time (ms) | Buffered Time (ms) | readAllLines Time (ms) |
|---|---:|---:|---:|
| 1 MB | 5195.669 | 24.269 | 67.813 |
| 10 MB | 44964.562 | 61.381 | 178.898 |
| 50 MB | 228259.634 | 213.190 | 427.879 |

All three methods returned the same line counts for each test file.

The results show that buffered reading performed best in all three experiments.

The measured execution times are specific to the test environment and may vary depending on system load, file-system caching and JVM warm-up.

## 7. Conclusion

The experiment demonstrates that reading a large file one byte at a time is inefficient because it requires a very large number of read method calls.

Using BufferedInputStream with an 8 KB buffer significantly reduces the number of Java read calls and improves execution time.

Files.readAllLines() is convenient for smaller files but requires memory to store all lines.

For large attendance log files, buffered reading is a suitable approach because it provides good performance while using a fixed-size buffer.

