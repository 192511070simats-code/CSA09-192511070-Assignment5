CREATE DATABASE IF NOT EXISTS b4_migration_test;

USE b4_migration_test;

CREATE TABLE IF NOT EXISTS bus_pass (
    pass_id VARCHAR(20) PRIMARY KEY,
    reg_no VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    route_no INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL
) ENGINE=InnoDB;
