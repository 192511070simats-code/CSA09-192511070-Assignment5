CREATE DATABASE IF NOT EXISTS b2_security_test;
USE b2_security_test;

CREATE TABLE IF NOT EXISTS demo_users (
    id VARCHAR(50) PRIMARY KEY,
    pwd VARCHAR(100) NOT NULL
);

DELETE FROM demo_users;

INSERT INTO demo_users (id, pwd)
VALUES ('student1', 'correct123');
