UnivERP: University Educational Resource Planning System

1. Overview
UnivERP is a desktop-based university management system developed using Java, Swing, and MySQL. The goal of the system is to provide a structured and reliable way for institutions to manage courses, sections, user accounts, grading workflows, and enrollment activity. The application separates responsibilities clearly across multiple layers so that the code remains maintainable and scalable as the system grows.

2. Architectural Structure
The system follows a strict three‑tier layered architecture:
- Presentation Layer: Contains all Swing user interface components for Admin, Instructor, and Student roles. The UI handles display, inputs, and interaction flow.
- Service Layer: Contains all business logic and validation. This layer decides whether an operation is allowed, performs checks such as maintenance mode and enrollment rules, and coordinates DAO calls.
- Data Access Layer: Contains DAO classes responsible for executing SQL queries against the MySQL databases. All read and write operations occur through these DAO classes.

Additional supporting components include authentication utilities and access control checks, both of which operate at the lower layers.

3. Security and Authentication
UnivERP maintains a clear separation between authentication data and academic data by using two different databases:
- auth_db: Stores usernames, BCrypt-hashed passwords, and user roles. No plain text passwords are stored.
- erp_db: Stores all academic and operational records, including course offerings, sections, enrollments, grades, and user profile details.

Password hashing is implemented using the jBCrypt library. Verification and hashing are isolated within dedicated utility classes. In addition, the system features a maintenance mode that can be enabled by an admin. When enabled, maintenance mode prevents non-admin users from modifying data until maintenance is complete.

4. Functional Features
4.1 Admin
The administrator has full control over academic structures and user entries. Typical actions include:
- Creating new user accounts and ensuring entries are written correctly to both authentication and profile tables.
- Creating courses, assigning instructors, defining sections, and adjusting capacity settings.
- Toggling maintenance mode, which restricts write operations across the entire system.

4.2 Instructor
Instructors are provided with tools to manage the sections assigned to them. These include:
- Viewing the list of sections they teach.
- Opening a gradebook interface to view enrolled students.
- Entering and updating grades for each student in a section. The service layer ensures that grading rules and access requirements are met.

4.3 Student
Students have access to functionalities that allow them to manage their academic activity, including:
- Viewing the list of available course sections.
- Enrolling in and dropping sections with appropriate checks for capacity and duplication.
- Accessing their timetable and grade records.

5. Data Flow and Processing
The layered approach ensures that all operations follow a predictable and controlled path. As an example, a student enrolling into a section follows this sequence:
1. The UI layer triggers the enrollment request through the StudentService.
2. The service layer performs checks for maintenance mode, section capacity, and duplicate enrollment.
3. If all conditions are met, the DAO layer writes the new enrollment entry to the database.
4. The UI reflects the result and updates the student’s schedule.

This flow prevents unauthorized operations, ensures consistent validation, and reduces redundancy.

6. Database Structure
The erp_db database contains core academic tables. These include:
- students and instructors: Profile and identification records for users.
- courses: High‑level catalog entries describing each course.
- sections: Individual offerings of each course, including time, assigned instructor, and capacity.
- enrollments: Many‑to‑many relationship between students and sections, with a unique constraint to prevent duplicates.
- grades: Records of assignment, test, and final evaluation data for each student.
- settings: Central configuration entries such as the maintenance‑mode flag.

7. Technologies Used
The system is implemented using:
- Java Swing for the graphical interface
- JDBC through MySQL Connector/J for database communication
- MySQL for structured data storage
- jBCrypt for secure password hashing
- DTO patterns for transferring lightweight and representation‑only data between layers

8. Conclusion
UnivERP provides a clear and extensible base for university resource management. Its layered architecture promotes stability and clean separation of responsibilities. The system is designed with long-term maintainability in mind and can be expanded with additional modules such as attendance, analytics, automated notifications, or workload management. The current implementation delivers the core functionalities needed for academic administration while maintaining strong security and consistency across all operations.
