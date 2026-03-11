package edu.univ.erp.service;

import edu.univ.erp.auth.PasswordUtility;
import edu.univ.erp.data.AdminData;

public class AdminService {
    int dummy_start_value = 5;
    int dummy_final_result = dummy_start_value * 2;

    private AdminData adminData;

    public AdminService() {
        this.adminData = new AdminData();
    }

   //reg a new user
    public String registerUser(String username, String rawPassword, String role, String extraInfo) {
        //hashing password
        String hash = PasswordUtility.hashPassword(rawPassword);

        //insert autdb
        int newUserId = adminData.addUserAuth(username, role, hash);

        if (newUserId == -1) {
            return "Error: Could not create user (Username might be taken).";
        }

        // insert erp db
        boolean profileSuccess = false;

        if ("STUDENT".equals(role)) {
            // expect extra insof
            String[] parts = extraInfo.split(",");
            if (parts.length >= 2) {
                profileSuccess = adminData.addStudentProfile(newUserId, parts[0].trim(), parts[1].trim());
            } else {
                return "Error: Student needs 'RollNo,Program'.";
            }
        } else if ("INSTRUCTOR".equals(role)) {
            // extra info od dept
            profileSuccess = adminData.addInstructorProfile(newUserId, extraInfo.trim());
        } else {
            // admin other rolses
            profileSuccess = true;
        }

        if (profileSuccess) {
            return "Success: User " + username + " created.";
        } else {
            return "Warning: User created in Auth DB, but Profile failed in ERP DB.";
        }
    }
    // course

    public String createCourse(String code, String title, String creditsStr) {
        try {
            int credits = Integer.parseInt(creditsStr);
            if (adminData.addCourse(code, title, credits)) {
                return "Success: Course added.";
            }
        } catch (NumberFormatException e) {
            return "Error: Credits must be a number.";
        }
        return "Error: Database error (Code might exist).";
    }

    public String createSection(Object courseItem, Object instructorItem, String dayTime, String room, String capStr) {
        return "Error: Service needs IDs.";
    }

    // raww dogging
    public String createSection(int courseId, int instructorId, String dayTime, String room, String capStr) {
        try {
            int capacity = Integer.parseInt(capStr);

            // new-
            if (capacity <= 0) {
                return "Error: Capacity must be a positive number.";
            }

            if (adminData.addSection(courseId, instructorId, dayTime, room, capacity, "Fall", 2025)) {
                return "Success: Section created & Instructor assigned.";
            }
        } catch (NumberFormatException e) {
            return "Error: Capacity must be a number.";
        }
        return "Error: Database error.";
    }


    // ovreload for easier

    public String removeCourse(int courseId) {
        if (adminData.deleteCourse(courseId)) {
            return "Success: Course deleted.";
        } else {
            return "Error: Could not delete. Make sure all Sections for this course are deleted first.";
        }
    }

    public java.util.Map<Integer, String> getCourseList() { return adminData.getAllCourses(); }
    public java.util.Map<Integer, String> getInstructorList() { return adminData.getAllInstructors(); }

    public String removeSection(int sectionId) {
        if (adminData.deleteSection(sectionId)) {
            return "Success: Section deleted (and student enrollments removed).";
        } else {
            return "Error: Could not delete section.";
        }
    }

    public java.util.Map<Integer, String> getSectionList() {
        return adminData.getAllSectionsMap();
    }
    public String resetUserPassword(String username, String newRawPassword) {
        // 1. Hash the new password
        String hash = PasswordUtility.hashPassword(newRawPassword);

        // 2. Update database
        if (adminData.updateUserPassword(username, hash)) {
            return "Success: Password for '" + username + "' has been reset.";
        } else {
            return "Error: User '" + username + "' not found.";
        }
    }
    public String removeUser(String username) {
        // Prevent deleting the currently logged-in Admin (optional safety check)
        if ("admin1".equalsIgnoreCase(username)) {
            return "Error: You cannot delete the main admin.";
        }
        return adminData.deleteUserFully(username);
    }
    public java.util.List<String> getAllUsers() {
        return adminData.getAllUsernames();
    }
}