package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.util.Properties;

//reads details from config/config.properties. change details there accordingly
public class DatabaseConnector {
    //Properties hold key value pair
    private static Properties props = new Properties();

    private static final String CONFIG_PATH = "config/config.properties";

    //this block runs once before the start
    static {
        try {
            java.io.File configFile = new java.io.File(CONFIG_PATH);
            if (!configFile.exists()) {
                configFile = new java.io.File("config.properties");
            }
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("CRITICAL ERROR: config file not found! Place config/config.properties or config.properties in project root.");
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