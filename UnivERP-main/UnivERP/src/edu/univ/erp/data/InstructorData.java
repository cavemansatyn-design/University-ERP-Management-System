package edu.univ.erp.data;

import edu.univ.erp.domain.CourseCatalog;
import edu.univ.erp.domain.EnrolledStudent;
import edu.univ.erp.domain.StudentScore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InstructorData {

    public List<CourseCatalog> getMySections(String instructorUsername) {
        List<CourseCatalog> list = new ArrayList<>();

        // wee join sections -> courses -> auth_db.users_auth (to filter by username)
        String sql = "SELECT s.section_id, c.code, c.title, c.credits, " +
                "       u.username AS instructor_name, s.day_time, s.room, s.capacity " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "JOIN auth_db.users_auth u ON s.instructor_id = u.user_id " +
                "WHERE u.username = ?";  // <--- The important filter

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, instructorUsername);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Reuse the CourseCatalogRowwww
                    list.add(new CourseCatalog(
                            rs.getInt("section_id"),
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getString("instructor_name"),
                            rs.getString("day_time"),
                            rs.getString("room"),
                            rs.getInt("credits"),
                            rs.getInt("capacity")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    // get all students enrolled in a spec sec
    public java.util.List<EnrolledStudent> getEnrolledStudents(int sectionId) {
        java.util.List<EnrolledStudent> list = new java.util.ArrayList<>();

        // JOIN enrollments -> students -> auth_db.users_auth
        String sql = "SELECT e.enrollment_id, u.username, s.roll_no, s.program " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.user_id " +
                "JOIN auth_db.users_auth u ON s.user_id = u.user_id " +
                "WHERE e.section_id = ? AND e.status = 'REGISTERED'";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new EnrolledStudent(
                            rs.getInt("enrollment_id"),
                            rs.getString("username"),
                            rs.getString("roll_no"),
                            rs.getString("program")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

   //save a gradee
    public boolean addGrade(int enrollmentId, String component, double score) {
        // Check if a grade already exists for this student + component of that whatever
        String checkSql = "SELECT grade_id FROM grades WHERE enrollment_id = ? AND component = ?";
        boolean exists = false;

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setInt(1, enrollmentId);
            stmt.setString(2, component);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    exists = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        //update or insert
        String sql;
        if (exists) {
            // Update
            sql = "UPDATE grades SET score = ? WHERE enrollment_id = ? AND component = ?";
        } else {
            // Insert
            sql = "INSERT INTO grades (score, enrollment_id, component) VALUES (?, ?, ?)";
        }

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            //update and insert

            if (exists) {
                stmt.setDouble(1, score);
                stmt.setInt(2, enrollmentId);
                stmt.setString(3, component);
            } else {
                stmt.setDouble(1, score);
                stmt.setInt(2, enrollmentId);
                stmt.setString(3, component);
            }

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // new ass method

    // define a new one
    public boolean addTestComponent(int sectionId, String testName) {
        String sql = "INSERT INTO section_tests (section_id, test_name) VALUES (?, ?)";
        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            stmt.setString(2, testName);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // defined listt
    public List<String> getTestComponents(int sectionId) {
        List<String> tests = new ArrayList<>();
        String sql = "SELECT test_name FROM section_tests WHERE section_id = ?";
        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) tests.add(rs.getString("test_name"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tests;
    }

    // get avg score
    public double getAverageScore(int sectionId, String component) {
        // Join grades -> enrollments to filter by section_id
        String sql = "SELECT AVG(g.score) FROM grades g " +
                "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                "WHERE e.section_id = ? AND g.component = ?";
        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            stmt.setString(2, component);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }
    //get listoall
    public java.util.List<StudentScore> getScoresForComponent(int sectionId, String component) {
        java.util.List<StudentScore> list = new java.util.ArrayList<>();

        // Join Enrollments -> Students -> Auth (for name)
        // LEFT JOIN Grades (to match specific component)
        String sql = "SELECT s.roll_no, u.username, g.score " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.user_id " +
                "JOIN auth_db.users_auth u ON s.user_id = u.user_id " +
                "LEFT JOIN grades g ON e.enrollment_id = g.enrollment_id AND g.component = ? " +
                "WHERE e.section_id = ? AND e.status = 'REGISTERED' " +
                "ORDER BY s.roll_no";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, component);
            stmt.setInt(2, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Check if score is null in DB
                    Double scoreVal = rs.getObject("score") != null ? rs.getDouble("score") : null;

                    list.add(new StudentScore(
                            rs.getString("roll_no"),
                            rs.getString("username"),
                            scoreVal
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}