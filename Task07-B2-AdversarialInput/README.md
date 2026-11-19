# B2 - Engineer an Adversarial Input

**Name:** K Suthesika
**Register Number:** 192511070
**Java Version:** JDK 21
**Database:** MySQL 8
**JDBC Driver:** MySQL Connector/J 26.10.0

## Contents
- src/B2DatabaseSetup.java - creates the local B2 database.
- src/B2LoginSecurityTest.java - compares unsafe SQL and PreparedStatement.
- src/B2PassStoreTest.java - tests damaged bus-pass records.
- database/schema.sql - database setup script.
- input/damaged-passes.csv - deliberately damaged records.
- B2-Analysis.md - experiment results and analysis.

## Compile
From the assignment root directory, run:
$base = "Task07-B2-AdversarialInput"
$lib = "Task08-B3-JDBC-Batch\lib\mysql-connector-j-26.10.0.jar"
javac -cp $lib -d "$base\out" "$base\src\B2DatabaseSetup.java" "$base\src\B2LoginSecurityTest.java" "$base\src\B2PassStoreTest.java"

## Run
Database setup: java -cp "$base\out;$lib" B2DatabaseSetup
SQL injection test: java -cp "$base\out;$lib" B2LoginSecurityTest
Damaged-file test: java -cp "$base\out" B2PassStoreTest

## Expected Results
Unsafe login accepted: true
PreparedStatement login accepted: false
Valid bus-pass records accepted: 2
Damaged or empty records rejected: 5

Both experiments compiled and ran successfully on JDK 21.
