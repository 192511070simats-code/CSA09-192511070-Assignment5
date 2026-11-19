USE assignment5_db;
ALTER TABLE wallets ENGINE=InnoDB;
ALTER TABLE demo_users ENGINE=InnoDB;
SHOW TABLE STATUS WHERE Name IN ('wallets', 'demo_users');
