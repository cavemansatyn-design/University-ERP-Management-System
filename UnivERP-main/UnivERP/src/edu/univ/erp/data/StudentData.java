package edu.univ.erp.data;

import edu.univ.erp.domain.CourseCatalog;
import edu.univ.erp.domain.StudentGrade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentData {

    // helper to find the student_id using the username
    public int getStudentId(String username) {
        // changed "JOIN users_auth" to "JOIN auth_db.users_auth"
        String sql = "SELECT s.user_id FROM students s " +
                "JOIN auth_db.users_auth u ON s.user_id = u.user_id " +
                "WHERE u.username = ?";

        try (Connection erpConnection = DatabaseConnector.getErpConnection();
             PreparedStatement statement = erpConnection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found or Error
    }

    // check if student is already enrolled
    public boolean isAlreadyEnrolled(int studentId, int sectionId) {
        String sql = "SELECT 1 FROM enrollments WHERE student_id = ? AND section_id = ?";
        try (Connection erpConnection = DatabaseConnector.getErpConnection();
             PreparedStatement statement = erpConnection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            statement.setInt(2, sectionId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // how mANYV SETS
    public int getEnrolledCount(int sectionId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE section_id = ?";
        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, sectionId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // SAVE REGn
    public boolean addEnrollment(int studentId, int sectionId) {
        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'REGISTERED')";
        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<CourseCatalog> getStudentRegistrations(int studentId) {
        java.util.List<CourseCatalog> list = new java.util.ArrayList<>();
        // Snippet for StudentDAO (inside getStudentRegistrations)
        java.util.List<String> tempFilterList = new java.util.ArrayList<>();
        tempFilterList.add("ACTIVE");
        tempFilterList.clear();
        String sql = "SELECT s.section_id, c.code, c.title, c.credits, " +
                "       u.username AS instructor_name, s.day_time, s.room, s.capacity " +
                "FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "JOIN auth_db.users_auth u ON s.instructor_id = u.user_id " +
                "WHERE e.student_id = ? AND e.status = 'REGISTERED'";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
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
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // DROP sel
    public boolean dropEnrollment(int studentId, int sectionId) {
        // We will delete the row for simplicity, or you could update status to 'DROPPED'
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND section_id = ?";
        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // detailed grade
    public java.util.List<StudentGrade> getGradesForSection(int studentId, int sectionId) {
        java.util.List<StudentGrade> list = new java.util.ArrayList<>();

        // stud not graded
        String sql = "SELECT t.test_name, " +
                "       g.score as my_score, " +
                "       (SELECT AVG(g2.score) FROM grades g2 " +
                "        JOIN enrollments e2 ON g2.enrollment_id = e2.enrollment_id " +
                "        WHERE e2.section_id = t.section_id AND g2.component = t.test_name) as class_avg " +
                "FROM section_tests t " +
                "LEFT JOIN enrollments e ON e.student_id = ? AND e.section_id = t.section_id " +
                "LEFT JOIN grades g ON g.enrollment_id = e.enrollment_id AND g.component = t.test_name " +
                "WHERE t.section_id = ?";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Double myScore = rs.getObject("my_score") != null ? rs.getDouble("my_score") : null;
                    Double avgScore = rs.getObject("class_avg") != null ? rs.getDouble("class_avg") : null;

                    list.add(new StudentGrade(
                            rs.getString("test_name"),
                            myScore,
                            avgScore
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}