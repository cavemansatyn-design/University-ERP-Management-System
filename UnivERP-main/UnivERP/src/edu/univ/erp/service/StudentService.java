package edu.univ.erp.service;

import edu.univ.erp.data.CourseData;
import edu.univ.erp.data.NotificationData;
import edu.univ.erp.data.StudentData; // Import the new DAO
import edu.univ.erp.domain.CourseCatalog;
import java.util.List;
import edu.univ.erp.access.AccessControl;
import edu.univ.erp.domain.StudentGrade;

public class StudentService {

    private CourseData courseData;
    private StudentData studentData; // Add this
    private AccessControl accessControl;

    public StudentService() {
        this.courseData = new CourseData();
        this.studentData = new StudentData();
        this.accessControl = new AccessControl(); // Initialize it
    }

    public List<CourseCatalog> getCatalog() {
        return courseData.getAvailableSections();
    }

    public String register(String username, int sectionId, int capacity) {
        // get id
        if (accessControl.isMaintenanceMode()) {
            return "BLOCKED: System is in Maintenance Mode. Registration is disabled.";
        }

        int studentId = studentData.getStudentId(username);
        if (studentId == -1) {
            return "Error: Student profile not found.";
        }

        // prevent duplicates
        if (studentData.isAlreadyEnrolled(studentId, sectionId)) {
            return "Error: You are already registered for this section.";
        }

        //check capac
        int currentCount = studentData.getEnrolledCount(sectionId);
        if (currentCount >= capacity) {
            return "Error: Section is full.";
        }

        // save
        boolean success = studentData.addEnrollment(studentId, sectionId);
        if (success) {
            return "Success: Registered successfully!";
        } else {
            return "Error: Database failure.";
        }
    }
    // get list
    public List<CourseCatalog> getMyRegistrations(String username) {
        int studentId = studentData.getStudentId(username);
        if (studentId == -1) return new java.util.ArrayList<>(); // Return empty list if error

        return studentData.getStudentRegistrations(studentId);
    }

    // Drop
    public String dropCourse(String username, int sectionId) {
        if (accessControl.isMaintenanceMode()) {
            return "BLOCKED: System is in Maintenance Mode. Dropping is disabled.";
        }
        int studentId = studentData.getStudentId(username);
        if (studentId == -1) return "Error: User not found.";

        boolean success = studentData.dropEnrollment(studentId, sectionId);
        if (success) {
            return "Success: Course dropped.";
        } else {
            return "Error: Could not drop course (maybe you aren't enrolled?)";
        }
    }
    // Add
    private NotificationData notificationData = new NotificationData();

    // Add this method
    public List<String> getNotifications(String username) {
        int studentId = studentData.getStudentId(username);
        if (studentId == -1) return new java.util.ArrayList<>();

        return notificationData.getMyNotifications(studentId);
    }
    public java.util.List<StudentGrade> viewGrades(String username, int sectionId) {
        int studentId = studentData.getStudentId(username);
        if (studentId == -1) return new java.util.ArrayList<>();

        return studentData.getGradesForSection(studentId, sectionId);
    }
}