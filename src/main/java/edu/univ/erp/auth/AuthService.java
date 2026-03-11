package edu.univ.erp.auth;

import edu.univ.erp.data.DatabaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    public String login(String username, String password) {
        String storedHash = null;
        String role = null;
        int failedAttempts = 0;

        //fetching details
        String sql = "SELECT password_hash, role, failed_attempts FROM users_auth WHERE username = ?";

        try (Connection conn = DatabaseConnector.getAuthConnection();
            //creates template for query
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    storedHash = rs.getString("password_hash");
                    role = rs.getString("role");
                    failedAttempts = rs.getInt("failed_attempts");
                }
                else {
                    //if user not found
                    return null;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


        if (PasswordUtility.checkPassword(password, storedHash)) {
            resetFailedAttempts(username);
            return role;

        }
        else {
            //failed
            int newCount = failedAttempts + 1;
            incrementFailedAttempts(username, newCount);

            //warning on every 5th attempt
            if (newCount % 5 == 0) {
                return "FAILURE_WARNING";
            }
            return null;
        }
    }

    private void incrementFailedAttempts(String username, int newCount) {

        String sql = "UPDATE users_auth SET failed_attempts = ? WHERE username = ?";

        try (Connection conn = DatabaseConnector.getAuthConnection();

             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, newCount);
            statement.setString(2, username);
            statement.executeUpdate();

        }
        catch (SQLException e) {
            e.printStackTrace();

        }

    }

    private void resetFailedAttempts(String username) {
        String sql = "UPDATE users_auth SET failed_attempts = 0, last_login = NOW() WHERE username = ?";

        try (Connection conn = DatabaseConnector.getAuthConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }
        catch (SQLException e) { e.printStackTrace(); }
    }



    public boolean changePassword(String username, String oldPassword, String newPassword) {

        String currentHash = null;

        //getting old password
        String sqlFetch = "SELECT password_hash FROM users_auth WHERE username = ?";

        try (Connection conn = DatabaseConnector.getAuthConnection(); PreparedStatement stmt = conn.prepareStatement(sqlFetch))
        {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    currentHash = rs.getString("password_hash");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        //checking password
        if (currentHash == null || !PasswordUtility.checkPassword(oldPassword, currentHash)) {
            return false;
        }

        //hashing new password
        String newHash = PasswordUtility.hashPassword(newPassword);


        String sqlUpdate = "UPDATE users_auth SET password_hash = ? WHERE username = ?";

        try (Connection connc = DatabaseConnector.getAuthConnection();
             PreparedStatement statement = connc.prepareStatement(sqlUpdate)) {

            statement.setString(1, newHash); //changes password
            statement.setString(2, username); //for username

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}