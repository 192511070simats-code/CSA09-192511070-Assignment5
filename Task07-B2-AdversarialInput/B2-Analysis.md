# B2 - Engineer an Adversarial Input

## 1. Objective

This task investigates adversarial inputs in SQL login queries and bus-pass file validation. A separate local MySQL database named b2_security_test was used.

## 2. SQL Injection Experiment

Test account:
- Username: student1
- Correct password: correct123

Crafted username:
' OR '1'='1' -- 

Incorrect password: wrong

Observed results:
- Unsafe login accepted: true
- PreparedStatement login accepted: false

The unsafe query concatenates user input into SQL. The crafted input adds an always-true condition and comments out the remaining password condition. Therefore, an invalid login is accepted.

PreparedStatement uses parameter placeholders and treats the crafted input as ordinary data. It rejects the invalid login.

## 3. Damaged Bus-Pass File Experiment

The damaged file contains:
- One record with too few fields.
- One non-numeric amount.
- One empty line.
- One empty name.
- One non-numeric route.
- Two valid records.

The original B1 implementation checks only the number of fields. It incorrectly accepts records containing a non-numeric amount, empty name, or non-numeric route.

The improved B2 validation checks:
- Exactly five fields.
- Non-empty values.
- Integer route number.
- Numeric, finite, non-negative amount.

Observed results:
- Valid records accepted: 2
- Damaged or empty records rejected: 5

## 4. Alternative SQL-Injection Defences

### Quote escaping

Escaping single quotes before building an SQL string may reduce injection risks. However, escaping depends on database rules, SQL context, character encoding, and correct implementation. An incorrect escaping method can leave vulnerabilities.

### Regular-expression validation

A username pattern such as [A-Za-z0-9_]{3,20} can restrict accepted characters. However, validation alone does not separate SQL syntax from input data. It may also reject legitimate usernames.

### Complexity analysis

Let L represent the input length.

| Approach | Approximate time cost |
|---|---|
| Quote escaping | O(L) |
| Simple regular-expression validation | O(L) |
| PreparedStatement parameter binding | O(L) for binding |

All three approaches process input in proportion to its length for these basic operations. PreparedStatement is the stronger design because it separates SQL instructions from supplied values.

## 5. Conclusion

The SQL injection experiment showed that unsafe SQL concatenation can accept an invalid login, while PreparedStatement rejects it. The file experiment showed that checking only the number of fields is insufficient. Complete structural and data-type validation correctly rejects damaged records.

Parameterized SQL and complete input validation improve the security and reliability of data persistence systems.
