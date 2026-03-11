# System Architecture

This document describes the layered architecture of the University ERP system.

---

## Overview

The application follows a **layered architecture** that separates concerns across five main layers. Each layer has a well-defined responsibility and communicates with adjacent layers in a structured way.

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer (Swing)                    │
├─────────────────────────────────────────────────────────┤
│                   Service Layer (Business Logic)         │
├─────────────────────────────────────────────────────────┤
│              Auth Layer │ Access Control Layer            │
├─────────────────────────────────────────────────────────┤
│                 Data Access Layer (JDBC)                 │
├─────────────────────────────────────────────────────────┤
│                   Domain Models (Entities)                │
└─────────────────────────────────────────────────────────┘
```

---

## UI Layer

**Location:** `edu.univ.erp.ui`

The UI layer handles all user interaction through **Java Swing** interfaces.

- **Login & Auth:** Login window, change-password dialog
- **Admin:** Admin dashboard, add/remove user, add/remove course, add/remove section, reset password, maintenance mode toggle
- **Instructor:** Instructor dashboard, gradebook window for entering and viewing grades
- **Student:** Student dashboard, course catalog view, enrollment/drop actions, timetable and grades

The UI layer does not contain business logic; it delegates all operations to the service layer and displays results.

---

## Service Layer

**Location:** `edu.univ.erp.service`

The service layer implements **business logic** and orchestrates data access.

- **AdminService:** User creation, course/section management, password reset (coordinates with Auth and Data layers)
- **InstructorService:** Section listing, gradebook data, grade submission (enforces access and maintenance checks)
- **StudentService:** Course catalog, enrollment, drop, timetable, grades (validates capacity and duplicate enrollment)

All validation, maintenance-mode checks, and workflow rules are enforced here before any database write.

---

## Data Layer

**Location:** `edu.univ.erp.data`

The data layer is responsible for **database operations** via **JDBC**.

- **DatabaseConnector:** Loads `config/config.properties` and provides connections to `auth_db` and `erp_db`
- **AdminData:** CRUD for users, courses, sections (ERP and auth tables)
- **StudentData:** Enrollments, catalog queries, grades, student profile
- **InstructorData:** Sections by instructor, enrolled students, grades (read/update)
- **CourseData:** Course and section catalog
- **NotificationData:** Insert and retrieve user notifications

All SQL is contained in this layer; no raw queries exist in the service or UI layers.

---

## Domain Layer

**Location:** `edu.univ.erp.domain`

The domain layer defines **entities** used across the application.

- **CourseCatalog:** Section view (course code, title, instructor, time, room, credits, capacity)
- **EnrolledStudent:** Student enrollment record for gradebook
- **StudentScore:** Grade component (e.g. assignment, test, final) with score
- **StudentGrade:** Aggregated grade view for a student

These are plain data holders (DTOs) with no business logic.

---

## Auth Layer

**Location:** `edu.univ.erp.auth`

The auth layer handles **authentication** and **password security**.

- **AuthService:** Login (username/password verification against `auth_db`), failed-attempt tracking, password change
- **PasswordUtility:** BCrypt hashing and verification (no plain-text storage)
- **ChangePassword:** Swing dialog for users to change their password

Passwords are stored only as BCrypt hashes in `auth_db`.

---

## Access Control

**Location:** `edu.univ.erp.access`

- **AccessControl:** Reads and updates the **maintenance mode** flag in `erp_db.settings`. When maintenance is on, non-admin write operations are blocked by the service layer.

Role-based access is enforced by the UI (which dashboard is shown after login) and by the service layer (which operations are allowed for the current role).

---

## Data Flow Example

**Student enrolls in a section:**

1. **UI:** Student selects a section and clicks "Enroll" → calls `StudentService.enroll(...)`
2. **Service:** Checks maintenance mode (`AccessControl`), section capacity, and duplicate enrollment; if valid, calls `StudentData` and `NotificationData`
3. **Data:** Inserts into `enrollments`, optionally adds notification
4. **UI:** Refreshes catalog and timetable

---

## Configuration

- **Config file:** `config/config.properties`
- **Databases:** Separate `auth_db` (credentials, roles) and `erp_db` (academic data)
- **Resources:** Images (e.g. logo) under `resources/images/` and loaded via classpath `/images/`

---

## Dependencies

- **Java Swing** – UI
- **JDBC / MySQL Connector/J** – Database
- **jBCrypt** – Password hashing
- **FlatLaf** – Look and feel for Swing

All layers use the **same package base** (`edu.univ.erp`) and are organized by subpackage (ui, service, data, domain, auth, access, util).
