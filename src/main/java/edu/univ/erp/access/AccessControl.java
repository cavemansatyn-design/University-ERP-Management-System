package edu.univ.erp.access;

import edu.univ.erp.data.DatabaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AccessControl {

    public boolean isMaintenanceMode() {
        String sql = "SELECT setting_value FROM settings WHERE setting_key = 'maintenance_on'";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {

                return "true".equalsIgnoreCase(rs.getString("setting_value"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //updating maintenence
    public boolean setMaintenanceMode(boolean isOn) {

        String sql = "UPDATE settings SET setting_value = ? WHERE setting_key = 'maintenance_on'";

        try (Connection conn = DatabaseConnector.getErpConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isOn ? "true" : "false");
            return stmt.executeUpdate() > 0;

        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}