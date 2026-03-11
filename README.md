# 🎓 University ERP System

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Swing](https://img.shields.io/badge/UI-Java%20Swing-2E7D32?style=flat-square)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![MySQL](https://img.shields.io/badge/Database-MySQL%20%2F%20JDBC-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://dev.mysql.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](https://opensource.org/licenses/MIT)

A **production-quality Java desktop application** for university resource planning. Implements role-based access control, secure authentication, course management, enrollment, grading, and administrative operations using a **layered architecture** with clear separation of concerns.

---

## 💡 Why This Project Stands Out

- **Clean Architecture** — UI, Service, Data, and Domain layers with no business logic in the presentation layer
- **Security-First** — BCrypt password hashing, dual-database design (auth vs. academic data), failed-login tracking
- **Production Patterns** — JDBC for data access, configurable properties, backup/restore, CSV import/export
- **Maintainable Codebase** — Modular packages, documented design decisions, and comprehensive documentation

---

## ✨ Features

| Feature | Description |
|--------|-------------|
| **Role-based access** | Admin, Instructor, and Student roles with dedicated dashboards and permission enforcement |
| **Admin management** | User CRUD, course/section management, maintenance mode, backup/restore, password reset |
| **Student management** | Course catalog, enrollment/drop, timetable, grade view |
| **Instructor management** | Assigned sections, gradebook with CSV import/export |
| **Course catalog** | Browse sections with capacity, schedule, room, and instructor |
| **Enrollment system** | Capacity checks, duplicate-enrollment prevention |
| **Authentication** | BCrypt hashing, change password, admin reset, failed-attempt tracking |
| **Notifications** | In-app notifications for enrollment and system events |

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|------------|
| **Language** | Java |
| **UI** | Java Swing (FlatLaf) |
| **Database** | JDBC, MySQL |
| **Security** | jBCrypt |
| **Architecture** | Layered (UI → Service → Data → Domain) |

---

## 📸 Application Screenshots

### Authentication & Entry

#### Login Page
![Login Page](docs/screenshots/login.png)

*Secure login screen with username and password fields. Authenticates against auth_db with BCrypt password hashing. Dark mode toggle and Change Password link available.*

#### Login (Dark Mode)
![Login Dark Mode](docs/screenshots/login-dark.png)

*Same login interface in dark theme. Demonstrates theme customization for user preference.*

#### Login Failed
![Login Failed](docs/screenshots/login-failed.png)

*Error dialog shown on invalid credentials. Clear feedback for authentication failures.*

---

### Admin Module

#### Admin Dashboard (Active)
![Admin Dashboard](docs/screenshots/admin-dashboard.png)

*System Administration panel with STATUS: ACTIVE. User management (Add/Remove User, Reset Password), course/section CRUD, maintenance mode, and database backup/restore.*

#### Admin Dashboard (Maintenance Mode)
![Admin Maintenance Mode](docs/screenshots/admin-maintenance.png)

*Dashboard with STATUS: MAINTENANCE MODE (ON). When enabled, non-admin write operations are blocked across the system.*

#### Add User
![Add User](docs/screenshots/add-user.png)

*Dialog to create new users. Username, password, role (Admin/Instructor/Student), roll number, and program (e.g. CSE).*

#### Remove User
![Remove User](docs/screenshots/remove-user.png)

*Search and select users to delete. Displays admin1, inst1, stu1, stu2, stu3. Red Delete Selected button for destructive action.*

#### Reset Password (Admin)
![Reset Password](docs/screenshots/reset-password.png)

*Admin dialog to reset any user's password. Search/select user, enter new password. Uses BCrypt for secure storage.*

#### Add Course
![Add Course](docs/screenshots/add-course.png)

*Create new courses with code (e.g. CS102), title, and credits. Save or cancel.*

#### Remove Course
![Remove Course](docs/screenshots/remove-course.png)

*Select course from dropdown (e.g. CS101) and delete. Confirmation required.*

#### Add Section
![Add Section](docs/screenshots/add-section.png)

*Create sections: select course, assign instructor, choose days (Mon–Fri), time slots (08:00–09:00), room, and capacity.*

#### Remove Section
![Remove Section](docs/screenshots/remove-section.png)

*Select section to delete (e.g. CS101: Mon,Wed 10:00–11:00 inst1). Database backup/restore visible in background.*

---

### Student Module

#### Student Dashboard — My Courses
![Student My Courses](docs/screenshots/student-courses.png)

*Enrolled courses with code, title, instructor, room. Actions: View Grades, Export Transcript, Drop Selected, Refresh.*

#### Student Dashboard — My Timetable
![Student Timetable](docs/screenshots/student-timetable.png)

*Personal schedule with day/time, course, title, room, instructor. Refresh Timetable button.*

---

### Instructor Module

#### Instructor Dashboard
![Instructor Dashboard](docs/screenshots/instructor-dashboard.png)

*Manage Sections and My Timetable tabs. Lists assigned sections with code, title, room, capacity. Open Gradebook and Refresh buttons.*

#### Gradebook — Enter Grades
![Gradebook Enter Grades](docs/screenshots/gradebook-enter.png)

*Manage assessments, student list (Roll No, Name, Program). Enter grades by assessment, Save Grade, Import from CSV.*

#### Gradebook — View Score Sheet
![Gradebook View Scores](docs/screenshots/gradebook-view.png)

*Select assessment to view scores. Load Scores, Export CSV, Show Average for analytics.*

---

## 📁 Project Structure

```
.
├── README.md
├── LICENSE
├── config/
│   ├── config.properties.example
│   └── config.properties          # DB credentials (gitignored)
├── docs/
│   ├── architecture.md            # Layer-by-layer architecture
│   ├── system-design.md           # Auth, RBAC, enrollment, grading
│   ├── uml-diagram.png
│   └── screenshots/
├── src/main/java/edu/univ/erp/
│   ├── Main.java
│   ├── auth/          # Authentication, BCrypt
│   ├── access/        # Maintenance mode, access control
│   ├── data/          # JDBC data access
│   ├── domain/        # Entities (CourseCatalog, StudentGrade, etc.)
│   ├── service/       # Business logic
│   ├── ui/            # Swing (admin, instructor, student)
│   └── util/          # CSV, backup, images
├── resources/images/
├── scripts/           # run.bat, run.sh, db setup
└── lib/               # FlatLaf, jBCrypt, MySQL Connector
```

---

## 🏗 Architecture

The system follows a **strict layered architecture**:

![System UML Diagram](docs/uml-diagram.png)

| Layer | Responsibility |
|-------|----------------|
| **UI** | Swing interfaces, user input, display |
| **Service** | Business logic, validation, orchestration |
| **Auth / Access** | Login, password hashing, maintenance mode |
| **Data** | JDBC queries, database operations |
| **Domain** | Entities and DTOs |

📖 **Full documentation:** [architecture.md](docs/architecture.md) · [system-design.md](docs/system-design.md)

---

## 🚀 How to Run

### Prerequisites

- **JDK 11+**
- **MySQL Server** with `auth_db` and `erp_db` created
- JARs in `lib/` (FlatLaf, jBCrypt, MySQL Connector)

### Quick Start

```bash
# 1. Clone
git clone <repo-url>
cd UnivERP-main

# 2. Database setup
# Run scripts/db/auth_db_setup.sql and scripts/db/erp_db_setup.sql in MySQL

# 3. Config
cp config/config.properties.example config/config.properties
# Edit config.properties with your MySQL credentials

# 4. Run (Windows)
scripts\run.bat

# Run (Linux/macOS)
chmod +x scripts/run.sh && ./scripts/run.sh
```

### Run in IntelliJ IDEA

1. **File → Open** → select project root
2. Right-click **`lib`** → **Add as Library**
3. Right-click **`Main.java`** → **Run 'Main.main()'**
4. If config not found: **Run → Edit Configurations** → set **Working directory** to project root

### Default Credentials (after DB seed)

| Role | Username | Password |
|------|----------|----------|
| Admin | admin1 | pass123 |
| Instructor | inst1 | pass123 |
| Student | stu1 | pass123 |

---

## 📚 Documentation

- [**Architecture**](docs/architecture.md) — Layer breakdown, data flow, configuration
- [**System Design**](docs/system-design.md) — Authentication, RBAC, course management, enrollment, notifications

---

## 🔮 Future Enhancements

- Web version with REST API
- Spring Boot migration
- React/Vue frontend
- Docker containerization

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit changes (`git commit -m 'Add feature'`)
4. Push (`git push origin feature/your-feature`)
5. Open a Pull Request

---

## 📄 License

MIT License — see [LICENSE](LICENSE).

---

<p align="center"><strong>University ERP System</strong> — A portfolio-ready Java application demonstrating layered architecture, security best practices, and clean code.</p>
