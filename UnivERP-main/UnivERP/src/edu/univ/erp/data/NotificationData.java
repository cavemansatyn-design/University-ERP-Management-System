package edu.univ.erp.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationData {



    // end a notificatio
    public void addNotification(int targetUserId, String message) {
        String sql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, targetUserId);
            stmt.setString(2, message);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // get unread notifications MRBEAST
    public List<String> getMyNotifications(int userId) {
        List<String> msgs = new ArrayList<>();
        String sql = "SELECT message, created_at FROM notifications WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String time = rs.getTimestamp("created_at").toString();
                    String text = rs.getString("message");
                    msgs.add("[" + time + "] " + text);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return msgs;
    }

    // eHelper-nosnfWe ned to find the student_id from an enrollment_id to know who to notify
    public int getStudentIdByEnrollment(int enrollmentId) {
        String sql = "SELECT student_id FROM enrollments WHERE enrollment_id = ?";
        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, enrollmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("student_id");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
}