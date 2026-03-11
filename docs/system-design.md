# System Design

This document describes the main subsystems and design decisions of the University ERP system.

---

## 1. Authentication System

### Overview

Authentication is handled by a dedicated **Auth** layer and a separate database (`auth_db`) to keep credentials isolated from academic data.

### Components

- **AuthService:** Validates username and password by loading the stored BCrypt hash from `users_auth` and using `PasswordUtility.checkPassword()`.
- **Password storage:** Only BCrypt hashes are stored; no plain-text passwords.
- **Failed attempts:** Failed login attempts are counted in `users_auth.failed_attempts`. After every 5th failure, the login response signals a warning (e.g. for account lockout or alerting).
- **Password change:** Users can change password via the Change Password dialog; `AuthService.changePassword()` verifies the old password, hashes the new one with BCrypt, and updates `auth_db`.
- **Default password reset:** Admins can reset a user’s password to a default value (e.g. `pass123`); the hash is generated with `PasswordUtility` and written to `auth_db`. A utility (e.g. `DefaultPassReset`) can be used to reset seed users to a known state.

### Security Properties

- Credentials and hashes live only in `auth_db`.
- All verification and hashing go through `PasswordUtility` (BCrypt).
- Failed-attempt tracking supports simple rate limiting and security monitoring.

---

## 2. Role-Based Access Control

### Roles

- **Admin:** Full access — user management, course/section management, maintenance mode, backup/restore, password reset.
- **Instructor:** Access to assigned sections, gradebook (view/enter grades), and related data.
- **Student:** Access to course catalog, enrollment/drop, own timetable and grades.

### Enforcement

- **At login:** `AuthService.login()` returns the user’s role; the UI opens the corresponding dashboard (Admin, Instructor, or Student).
- **In the service layer:** Business operations check the effective role (and maintenance mode) before performing updates. For example, only admins can create users or toggle maintenance; only instructors assigned to a section can update grades.
- **Maintenance mode:** Stored in `erp_db.settings` (e.g. `maintenance_on`). When enabled, non-admin write operations are blocked in the service layer, restricting data changes during maintenance.

---

## 3. Course Management

### Structure

- **Courses:** Stored in `courses` (course_id, code, title, credits). Represent the catalog of offerings.
- **Sections:** Stored in `sections` (section_id, course_id, instructor_id, day_time, room, capacity, semester, year). Each section is one offering of a course with time, place, and capacity.

### Admin Workflow

- **Add course:** Admin creates a course (code, title, credits) via Admin UI → `AdminService` → `AdminData` → insert into `courses`.
- **Add section:** Admin creates a section (course, instructor, time, room, capacity, semester, year) → `AdminService` → `AdminData` → insert into `sections`.
- **Remove course/section:** Admin can remove sections and courses; design may use cascades or checks so that enrollments and grades are handled consistently (e.g. prevent delete if enrollments exist or cascade as per schema).

Course and section data are read by students (catalog) and instructors (assigned sections) through the same data layer, with access filtered by role in the service layer.

---

## 4. Grade Management

### Data Model

- **section_tests:** Defines grade components per section (e.g. Assignment, Midterm, Final) with optional max marks.
- **grades:** Links to `enrollments`; stores component name and score per enrollment (per student per section).
- **EnrolledStudent / StudentScore:** Domain objects used by the gradebook to show students and their scores.

### Instructor Workflow

- Instructor opens the gradebook for a section → `InstructorService` loads enrolled students and existing grades from `InstructorData`.
- Instructor enters or updates scores → service layer validates (e.g. instructor assigned to section, maintenance off) → `InstructorData` updates `grades`.
- Optional: CSV import/export for bulk score entry (e.g. via `CsvUtility` and gradebook UI).

### Student View

- Students see their own grades through the student dashboard → `StudentService` / `StudentData` return grades only for the logged-in student.

---

## 5. Student Enrollment

### Rules

- A student can enroll only in sections that have available capacity.
- A student cannot enroll twice in the same section (enforced by unique constraint and/or service-layer check).
- Enrollment may be blocked when maintenance mode is on (enforcement in service layer).

### Flow

1. Student selects a section from the catalog (read from `CourseData` / `StudentData`).
2. Student clicks Enroll → `StudentService.enroll(studentId, sectionId)`.
3. Service checks maintenance mode, capacity, and duplicate enrollment; if valid, calls `StudentData` to insert into `enrollments` (status e.g. REGISTERED).
4. Optional: `NotificationData` adds a notification for the student or instructor.
5. UI refreshes catalog and timetable.

### Drop

- Student can drop a section → service validates (e.g. ownership, maintenance off) → update or delete enrollment (e.g. status DROPPED or delete as per schema).

---

## 6. Notification System

### Purpose

Notify users of events (e.g. enrollment confirmed, grade posted, admin message).

### Implementation

- **NotificationData:** Inserts into `notifications` (e.g. user_id, message, created_at) and retrieves notifications for a user.
- **Storage:** Table such as `notifications (user_id, message, created_at)` in `erp_db`.
- **Usage:** Service layer calls `NotificationData.addNotification(targetUserId, message)` after events (e.g. after enrollment). Dashboards can call `getMyNotifications(userId)` to display unread or recent notifications.

Notifications are scoped by `user_id`, so each role (admin, instructor, student) sees only their own notifications when the UI requests them.

---

## Summary

| Subsystem           | Key components                          | Storage / API                    |
|---------------------|------------------------------------------|----------------------------------|
| Authentication      | AuthService, PasswordUtility             | auth_db.users_auth               |
| Role-based access   | UI routing, Service checks, AccessControl| erp_db.settings (maintenance)    |
| Course management   | AdminService, AdminData, CourseData      | courses, sections                |
| Grade management    | InstructorService, InstructorData        | section_tests, grades, enrollments|
| Student enrollment  | StudentService, StudentData              | enrollments                      |
| Notifications       | NotificationData                         | notifications                    |

This design keeps authentication separate, centralizes business rules in the service layer, and uses the data layer for all database access, making the system easier to maintain and extend.
