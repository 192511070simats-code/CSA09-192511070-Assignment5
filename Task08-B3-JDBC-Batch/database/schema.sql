CREATE DATABASE IF NOT EXISTS b3_jdbc_test;
USE b3_jdbc_test;

DROP TABLE IF EXISTS bus_pass;

CREATE TABLE bus_pass (
    pass_id VARCHAR(20) PRIMARY KEY,
    reg_no VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    route_no INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL
);
