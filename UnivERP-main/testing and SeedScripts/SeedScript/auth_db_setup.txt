CREATE DATABASE IF NOT EXISTS auth_db;
USE auth_db;

DROP TABLE IF EXISTS users_auth;


CREATE TABLE users_auth (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    role ENUM('STUDENT', 'INSTRUCTOR', 'ADMIN') NOT NULL,
    password_hash VARCHAR(255) NOT NULL, 
    status ENUM('ACTIVE', 'LOCKED') NOT NULL DEFAULT 'ACTIVE',
    last_login TIMESTAMP,
    failed_attempts INT DEFAULT 0
);


INSERT INTO users_auth (username, role, password_hash, failed_attempts) VALUES
('admin1', 'ADMIN', '$2a$10$H92Ai6KUyc88t.4pEwut4.zCCBnd6PX57yvdjq0gfYLYt5DXqe8C6', 0),
('inst1', 'INSTRUCTOR', '$2a$10$H92Ai6KUyc88t.4pEwut4.zCCBnd6PX57yvdjq0gfYLYt5DXqe8C6', 0),
('stu1', 'STUDENT', '$2a$10$H92Ai6KUyc88t.4pEwut4.zCCBnd6PX57yvdjq0gfYLYt5DXqe8C6', 0),
('stu2', 'STUDENT', '$2a$10$H92Ai6KUyc88t.4pEwut4.zCCBnd6PX57yvdjq0gfYLYt5DXqe8C6', 0);