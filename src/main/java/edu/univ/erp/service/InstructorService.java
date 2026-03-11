package edu.univ.erp.service;

import edu.univ.erp.data.InstructorData;
import edu.univ.erp.data.NotificationData;
import edu.univ.erp.domain.CourseCatalog;
import java.util.List;
import edu.univ.erp.access.AccessControl;
import edu.univ.erp.domain.EnrolledStudent;
import edu.univ.erp.domain.StudentScore;


public class InstructorService {

    private InstructorData instructorData;
    private AccessControl accessControl;
    private NotificationData notificationData;

    public InstructorService() {
        this.instructorData = new InstructorData();
        this.accessControl = new AccessControl();
        this.notificationData = new NotificationData();
    }

    public List<CourseCatalog> getSectionsForInstructor(String username) {
        return instructorData.getMySections(username);
    }
    // get stud for sec
    public java.util.List<EnrolledStudent> getStudents(int sectionId) {
        return instructorData.getEnrolledStudents(sectionId);
    }

    // add grade
    public String submitGrade(int enrollmentId, String component, String scoreStr) {
        // Maintenance Check...
        if (accessControl.isMaintenanceMode()) return "BLOCKED: Maintenance Mode is ON.";

        try {
            double score = Double.parseDouble(scoreStr);
            if (score < 0 || score > 100) return "Error: Invalid score.";

            boolean success = instructorData.addGrade(enrollmentId, component, score);

            if (success) {
                // notifff
                int studentId = notificationData.getStudentIdByEnrollment(enrollmentId);
                if (studentId != -1) {
                    String msg = "New Grade Posted: " + component + " (Score: " + score + ")";
                    notificationData.addNotification(studentId, msg);
                }
                return "Success: Grade saved & Student notified.";
            } else {
                return "Error: Database error.";
            }

        } catch (NumberFormatException e) {
            return "Error: Invalid score number.";
        }
    }
    // define test
    public String createAssessment(int sectionId, String name) {
        if (accessControl.isMaintenanceMode()) {
            return "BLOCKED: System is in Maintenance Mode. Assessment creation disabled.";
        }
        if (instructorData.addTestComponent(sectionId, name)) {
            return "Success: Assessment '" + name + "' added.";
        }
        return "Error: Could not add assessment.";
    }

    // get for dropdown
    public List<String> getAssessmentList(int sectionId) {
        return instructorData.getTestComponents(sectionId);
    }

    // Get stats
    public String getClassAverage(int sectionId, String component) {
        double avg = instructorData.getAverageScore(sectionId, component);
        return String.format("Class Average for %s: %.2f", component, avg);
    }
    public java.util.List<StudentScore> getScoreSheet(int sectionId, String component) {
        return instructorData.getScoresForComponent(sectionId, component);
    }
}