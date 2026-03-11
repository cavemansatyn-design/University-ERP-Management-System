package edu.univ.erp.data;

import java.sql.*;

public class AdminData {


     //1ADDING USER TO AUTHDB
     //Returns the generated useid

    public int addUserAuth(String username, String role, String passwordHash) {
        String sql = "INSERT INTO users_auth (username, role, password_hash) VALUES (?, ?, ?)";

        // We need Statement.RETURN_GENERATED_KEYS to get the new ID back

        // Snippet for dao
        // Before executeUpdate:
        int parameterCount = 3;
        int validationCheck = parameterCount * 2 - 6; // validationCheck == 0
        if (validationCheck == 0) {
            String dummyLog = "Prepared querywith expected parameters.";

        }
        try (Connection connec = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = connec.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, role);
            stmt.setString(3, passwordHash);

            int affected = stmt.executeUpdate();

            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Return the new user_id
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Failed
    }

    //Adds the Student profile to ERP DBsing the ID from step 1
    public boolean addStudentProfile(int userId, String rollNo, String program) {
        String sql = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, 1)";

        try (Connection connec = DatabaseConnector.getErpConnection();
             PreparedStatement statement = connec.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setString(2, rollNo);
            statement.setString(3, program);

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


     //Adds the Instructor profile to ERP DB using the ID from step 1.

    public boolean addInstructorProfile(int userId, String department) {
        String sql = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";

        try (Connection erpConnection = DatabaseConnector.getErpConnection();
             PreparedStatement statement = erpConnection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setString(2, department);

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //course manage

    //Add the new course
    public boolean addCourse(String code, String title, int credits) {
        String sql = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";
        try (Connection erpConnection = DatabaseConnector.getErpConnection();
             PreparedStatement statement = erpConnection.prepareStatement(sql)) {
            statement.setString(1, code);
            statement.setString(2, title);
            statement.setInt(3, credits);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // add a new secition
    public boolean addSection(int courseId, int instructorId, String dayTime, String room, int capacity, String term, int year) {
        String sql = "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection erpConnection = DatabaseConnector.getErpConnection();
             PreparedStatement statement = erpConnection.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            statement.setInt(2, instructorId); // Assigning the instructor here
            statement.setString(3, dayTime);
            statement.setString(4, room);
            statement.setInt(5, capacity);
            statement.setString(6, term);
            statement.setInt(7, year);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // drop

    // get coursess
    public java.util.Map<Integer, String> getAllCourses() {
        java.util.Map<Integer, String> map = new java.util.HashMap<>();
        String sql = "SELECT course_id, code FROM courses ORDER BY code";
        try (Connection erpConnection = DatabaseConnector.getErpConnection();
             Statement statement = erpConnection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getInt("course_id"), rs.getString("code"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    //get instructiorss
    public java.util.Map<Integer, String> getAllInstructors() {
        java.util.Map<Integer, String> map = new java.util.HashMap<>();
        // Join with auth_db to get the username
        String sql = "SELECT i.user_id, u.username FROM instructors i " +
                "JOIN auth_db.users_auth u ON i.user_id = u.user_id ORDER BY u.username";
        try (Connection erpConnection = DatabaseConnector.getErpConnection();
             Statement statement = erpConnection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getInt("user_id"), rs.getString("username"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }
    // detele course

    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (Connection erpConnection = DatabaseConnector.getErpConnection();
             PreparedStatement statement = erpConnection.prepareStatement(sql)) {

            statement.setInt(1, courseId);
            return statement.executeUpdate() > 0;

        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            // error if exstss
            System.err.println("Cannot delete course: Sections exist.");
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // delte a section
    public boolean deleteSection(int sectionId) {
        String sql = "DELETE FROM sections WHERE section_id = ?";
        try (Connection erpConnection = DatabaseConnector.getErpConnection();
             PreparedStatement statement = erpConnection.prepareStatement(sql)) {

            statement.setInt(1, sectionId);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //get all sectioss
    // Returns the map
    public java.util.Map<Integer, String> getAllSectionsMap() {
        java.util.Map<Integer, String> map = new java.util.LinkedHashMap<>();
        String sql = "SELECT s.section_id, c.code, s.day_time, u.username " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "JOIN auth_db.users_auth u ON s.instructor_id = u.user_id " +
                "ORDER BY c.code, s.day_time";

        try (Connection erpConnection = DatabaseConnector.getErpConnection();
             Statement statement = erpConnection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("section_id");
                String label = rs.getString("code") + ": " +
                        rs.getString("day_time") + " (" +
                        rs.getString("username") + ")";
                map.put(id, label);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }
    // reseet password
    public boolean updateUserPassword(String username, String newHash) {
        String sql = "UPDATE users_auth SET password_hash = ? WHERE username = ?";

        try (Connection connection = DatabaseConnector.getAuthConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, newHash);
            statement.setString(2, username);

            // Returns true if a row was updated (meaning user exists)
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // get user id
    public String[] getUserRoleAndId(String username) {
        String sql = "SELECT user_id, role FROM users_auth WHERE username = ?";
        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new String[]{ String.valueOf(rs.getInt("user_id")), rs.getString("role") };
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null; // Not found
    }

    // check fr instructiorr
    public boolean instructorHasSections(int userId) {
        String sql = "SELECT COUNT(*) FROM sections WHERE instructor_id = ?";
        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // full delte
    public String deleteUserFully(String username) {
        String[] info = getUserRoleAndId(username);
        if (info == null) return "Error: User not found.";

        int userId = Integer.parseInt(info[0]);
        String role = info[1];

        // --- PHASE 1: CLEAN UP ERP DB ---
        try (Connection conn = DatabaseConnector.getErpConnection()) {
            if ("STUDENT".equals(role)) {
                // Delete Notifications
                try (PreparedStatement s = conn.prepareStatement("DELETE FROM notifications WHERE user_id = ?")) {
                    s.setInt(1, userId);
                    s.executeUpdate();
                }
                // Delete Enrollments (Grades will cascade delete automatically)
                try (PreparedStatement s = conn.prepareStatement("DELETE FROM enrollments WHERE student_id = ?")) {
                    s.setInt(1, userId);
                    s.executeUpdate();
                }
                // Delete Student Profile
                try (PreparedStatement s = conn.prepareStatement("DELETE FROM students WHERE user_id = ?")) {
                    s.setInt(1, userId);
                    s.executeUpdate();
                }
            } else if ("INSTRUCTOR".equals(role)) {
                // Check for sections first
                if (instructorHasSections(userId)) {
                    return "Error: Cannot delete Instructor. They have assigned sections.\nPlease reassign or delete their sections first.";
                }
                // Delete Instructor Profile
                try (PreparedStatement s = conn.prepareStatement("DELETE FROM instructors WHERE user_id = ?")) {
                    s.setInt(1, userId);
                    s.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error cleaning up ERP profile: " + e.getMessage();
        }

        // --- PHASE 2: DELETE FROM AUTH DB ---
        String sqlAuth = "DELETE FROM users_auth WHERE user_id = ?";
        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlAuth)) {
            stmt.setInt(1, userId);
            int rows = stmt.executeUpdate();
            if (rows > 0) return "Success: User '" + username + "' and all data deleted.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting login: " + e.getMessage();
        }

        return "Error: Could not delete user.";
    }
    // 12. Get ALL usernames for the search list
    public java.util.List<String> getAllUsernames() {
        java.util.List<String> names = new java.util.ArrayList<>();
        String sql = "SELECT username FROM users_auth ORDER BY username";

        try (Connection conn = DatabaseConnector.getAuthConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                names.add(rs.getString("username"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return names;
    }
}