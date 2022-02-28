DROP DATABASE IF EXISTS hootsDB;
CREATE DATABASE hootsDB CHARACTER SET = 'utf8mb4';
CREATE USER IF NOT EXISTS '__user__'@'localhost' IDENTIFIED BY '__password__';
GRANT ALL PRIVILEGES on hootsDB.* to '__user__'@'localhost';
FLUSH privileges;