package edu.univ.erp.util;

import edu.univ.erp.auth.PasswordUtility;
import edu.univ.erp.data.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;


public class DefaultPassReset {

    public static void main(String[] args) {

        String newPlainTextPassword = "pass123";

        System.out.println("Starting password reset process");
        System.out.println("New password will be: " + newPlainTextPassword);


        String newHash = PasswordUtility.hashPassword(newPlainTextPassword);
        System.out.println("Generated new hash: " + newHash);


        String sql = "UPDATE users_auth SET password_hash = ? WHERE username IN ('admin1', 'inst1', 'stu1', 'stu2')";


        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            //set the new hash as the parameter
            stmt.setString(1, newHash);

            //execute the update
            int rowsAffected = stmt.executeUpdate();

            System.out.println("----------------------------------------");
            System.out.println("SUCCESS: Database updated.");
            System.out.println(rowsAffected + " user passwords have been reset.");
            System.out.println("----------------------------------------");

        } catch (Exception e) {
            System.err.println("----------------------------------------");
            System.err.println("ERROR: Could not update passwords in database.");
            System.err.println("----------------------------------------");
            e.printStackTrace();
        }
    }
}