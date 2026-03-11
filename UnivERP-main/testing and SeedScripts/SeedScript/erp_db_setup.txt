CREATE DATABASE IF NOT EXISTS erp_db;
USE erp_db;


SET FOREIGN_KEY_CHECKS = 0; 
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS section_tests;
DROP TABLE IF EXISTS grades;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS sections;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS instructors;
DROP TABLE IF EXISTS settings;
SET FOREIGN_KEY_CHECKS = 1;




CREATE TABLE students (
    user_id INT PRIMARY KEY,
    roll_no VARCHAR(20) NOT NULL UNIQUE,
    program VARCHAR(100),
    year INT
);


CREATE TABLE instructors (
    user_id INT PRIMARY KEY,
    department VARCHAR(100)
);


CREATE TABLE courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    credits INT
);


CREATE TABLE sections (
    section_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    instructor_id INT,
    day_time VARCHAR(100),
    room VARCHAR(50),
    capacity INT NOT NULL,
    semester VARCHAR(50),
    year INT,
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (instructor_id) REFERENCES instructors(user_id)
);


CREATE TABLE section_tests (
    test_id INT AUTO_INCREMENT PRIMARY KEY,
    section_id INT NOT NULL,
    test_name VARCHAR(50) NOT NULL,
    max_marks INT DEFAULT 100,
    FOREIGN KEY (section_id) REFERENCES sections(section_id) ON DELETE CASCADE
);


CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    section_id INT NOT NULL,
    status ENUM('REGISTERED', 'DROPPED', 'COMPLETED') NOT NULL DEFAULT 'REGISTERED',
    FOREIGN KEY (student_id) REFERENCES students(user_id),
    FOREIGN KEY (section_id) REFERENCES sections(section_id) ON DELETE CASCADE,
    UNIQUE KEY uk_student_section (student_id, section_id)
);


CREATE TABLE grades (
    grade_id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT NOT NULL,
    component VARCHAR(50) NOT NULL,
    score DECIMAL(5, 2),
    final_grade VARCHAR(2),
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    /* Ensures a student only has one grade per component */
    UNIQUE KEY uk_grade (enrollment_id, component)
);


CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message VARCHAR(255) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES students(user_id)
);


CREATE TABLE settings (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(100)
);




INSERT INTO instructors (user_id, department) VALUES (2, 'Computer Science');
INSERT INTO students (user_id, roll_no, program, year) VALUES (3, '2025001', 'B.Tech CS', 2);
INSERT INTO students (user_id, roll_no, program, year) VALUES (4, '2025002', 'B.Tech ECE', 2);

INSERT INTO settings (setting_key, setting_value) VALUES ('maintenance_on', 'false');


INSERT INTO courses (code, title, credits) VALUES ('CS101', 'Intro to Programming', 4);


INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) 
VALUES (1, 2, 'Mon,Wed 10:00-11:00', 'R101', 30, 'Fall', 2025);