# A5 – Design for Correctness and Safety

## A5.1 – Transaction Design

### Problem
A student transfers Rs. 500 from a hostel wallet to a canteen wallet. Both wallet updates must succeed together. If either update fails, neither balance should change.

### JDBC transaction approach
1. Obtain a database connection.
2. Disable auto-commit using `setAutoCommit(false)`.
3. Subtract Rs. 500 from the hostel wallet.
4. Add Rs. 500 to the canteen wallet.
5. If both updates succeed, call `commit()`.
6. If an error occurs, call `rollback()` to cancel all changes.
7. Close the connection and resources.

### Pseudocode

```text
BEGIN
    connection = getDatabaseConnection()
    connection.setAutoCommit(false)

    TRY
        UPDATE hostel_wallet
        SET balance = balance - 500
        WHERE wallet_id = 'HOSTEL'
          AND balance >= 500

        IF affected_rows != 1
            THROW transaction_error

        UPDATE canteen_wallet
        SET balance = balance + 500
        WHERE wallet_id = 'CANTEEN'

        IF affected_rows != 1
            THROW transaction_error

        connection.commit()
        DISPLAY "Transfer successful"

    CATCH error
        connection.rollback()
        DISPLAY "Transfer cancelled"

    FINALLY
        CLOSE connection
END
```

The two updates are part of one database transaction. `commit()` permanently saves both updates. `rollback()` cancels both updates if an error occurs before the commit. This prevents a partial transfer, assuming the database tables use a transactional storage engine such as InnoDB.

### Practical test results

Initial balances:
- Hostel wallet: Rs. 1,000
- Canteen wallet: Rs. 200
- Total: Rs. 1,200

**Failure test:** A simulated error was raised after the hostel debit and before the canteen credit. The rollback restored both balances to Rs. 1,000 and Rs. 200.

**Successful test:** Both updates were committed. The hostel balance became Rs. 500 and the canteen balance became Rs. 700. The total remained Rs. 1,200.

## A5.2 – SQL Injection Analysis

### Vulnerable query

```java
String sql =
    "SELECT * FROM users WHERE id='" + id +
    "' AND pwd='" + pwd + "'";
```

This query directly joins user input with SQL commands. A crafted input can change the meaning of the query.

### Example of crafted input

User ID:
```text
' OR '1'='1' --
```

Password:
```text
wrong
```

The resulting query is similar to:

```sql
SELECT * FROM users
WHERE id='' OR '1'='1' -- ' AND pwd='wrong'
```

The condition `'1'='1'` is always true. The `-- ` sequence comments out the remaining password condition in MySQL. Therefore, the vulnerable query may return a user record even when the password is incorrect.

### Safe redesign using PreparedStatement

```java
String sql =
    "SELECT * FROM users WHERE id=? AND pwd=?";

PreparedStatement ps = connection.prepareStatement(sql);
ps.setString(1, id);
ps.setString(2, pwd);

ResultSet rs = ps.executeQuery();

if (rs.next()) {
    System.out.println("Login successful");
} else {
    System.out.println("Invalid login");
}
```

PreparedStatement separates SQL instructions from user-supplied values. The crafted input is treated as ordinary data instead of executable SQL. Therefore, it cannot change the query structure.

For a real application, passwords should also be stored as securely salted password hashes, not plaintext values.

### Practical security test

The test used a sample account and a deliberately incorrect password.

- Unsafe concatenated query: login accepted.
- PreparedStatement query: login rejected.

This demonstrates why parameterized queries are safer against SQL injection.

## Alternative Concept and Complexity Analysis

### Two separate auto-commit updates

When auto-commit is enabled, each SQL update is committed independently.

If the hostel debit succeeds and the program fails before the canteen credit:
- Rs. 500 is deducted from the hostel wallet.
- The canteen wallet receives nothing.
- Rs. 500 is lost from the combined wallet balances.

If the credit happens first and the debit fails, the canteen wallet could receive Rs. 500 without the corresponding deduction. This creates money.

### Complexity comparison

| Aspect | One transaction | Two auto-commit updates |
|---|---|---|
| Database updates | Two | Two |
| Time complexity | O(1) for indexed wallet IDs | O(1) for indexed wallet IDs |
| Extra overhead | Transaction, logging and locking overhead | Separate commit overhead |
| Failure handling | Both updates roll back together | First committed update remains |
| Data correctness | Maintains transfer consistency | Can cause lost or created money |

The asymptotic time complexity is the same for this fixed number of indexed updates. A transaction has additional coordination and commit overhead, but it protects correctness. The small performance overhead is justified for financial operations.

## Conclusion

JDBC transactions protect wallet transfers by grouping related updates into one atomic operation. `commit()` saves both changes, while `rollback()` cancels them together when an error occurs. PreparedStatement protects login queries by treating user input as data rather than SQL instructions. Transactions and parameterized queries are important techniques for building correct and secure database applications.
