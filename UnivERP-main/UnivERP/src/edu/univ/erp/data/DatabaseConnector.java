package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.util.Properties;

//reads details from config.properties. change details there accordingly
public class DatabaseConnector {
    //Properties hold key value pair
    private static Properties props = new Properties();

    //this block runs once before the start
    static {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("CRITICAL ERROR: config.properties file not found!");
        }
    }

    //auth db connections
    private static final String AUTH_DB_URL = props.getProperty("auth.db.url");
    private static final String AUTH_USER = props.getProperty("auth.db.user");
    private static final String AUTH_PASS = props.getProperty("auth.db.pass");

    public static Connection getAuthConnection() throws SQLException {
        return DriverManager.getConnection(AUTH_DB_URL, AUTH_USER, AUTH_PASS);
    }


    //erp db connections
    private static final String ERP_DB_URL = props.getProperty("erp.db.url");
    private static final String ERP_USER = props.getProperty("erp.db.user");
    private static final String ERP_PASS = props.getProperty("erp.db.pass");

    public static Connection getErpConnection() throws SQLException {

        return DriverManager.getConnection(ERP_DB_URL, ERP_USER, ERP_PASS);
    }

}