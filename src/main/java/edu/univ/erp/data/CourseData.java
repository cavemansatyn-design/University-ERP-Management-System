package edu.univ.erp.data;

import edu.univ.erp.domain.CourseCatalog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CourseData {

    public List<CourseCatalog> getAvailableSections() {
        List<CourseCatalog> catalogList = new ArrayList<>();



        // fix here
        // We use "auth_db.users_auth" to tell MySQL to look in the auth database
        String sql = "SELECT s.section_id, c.code, c.title, c.credits, " +
                "       u.username AS instructor_name, s.day_time, s.room, s.capacity " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "JOIN auth_db.users_auth u ON s.instructor_id = u.user_id " + // fix line
                "ORDER BY c.code";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CourseCatalog row = new CourseCatalog(
                        rs.getInt("section_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getString("instructor_name"),
                        rs.getString("day_time"),
                        rs.getString("room"),
                        rs.getInt("credits"),
                        rs.getInt("capacity")
                );
                catalogList.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return catalogList;
    }
}