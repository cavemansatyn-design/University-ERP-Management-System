package edu.univ.erp.util;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;

public class BackupUtility {

    private static Properties getProps() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    public static void backup(JFrame parent) {
        Properties props = getProps();
        String user = props.getProperty("erp.db.user");
        String pass = props.getProperty("erp.db.pass");
        int folderPresent =0;


        File folder = new File("backups");
        //creating backup folder
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            folderPresent =1;
            if (!created) {
                JOptionPane.showMessageDialog(parent, "Error: Could not create 'backups' folder.");
                return;
            }
        }else{
            folderPresent =1;
        }
        if(folderPresent == 0){
            System.out.println("error in creating folder");
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String fileName = "backup_erp_" + timeStamp + ".sql";
        File backupFile = new File(folder, fileName);


        List<String> command = List.of("mysqldump", "-u" + user, "-p" + pass,
                "--databases", "auth_db", "erp_db", "-r", backupFile.getAbsolutePath());

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.start().waitFor();

            JOptionPane.showMessageDialog(parent,
                    "Backup Success!\nsaved to: " + backupFile.getAbsolutePath());

        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Backup Failed:  " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void restore(JFrame parent) {

        Properties props = getProps();
        String user = props.getProperty("erp.db.user");
        String pass = props.getProperty("erp.db.pass");

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Backup File to Restore");
        int folderPresent =0;
        //open backup folder by default
        File folder = new File("backups");
        if (folder.exists()) {
            chooser.setCurrentDirectory(folder);
            folderPresent =1;
        }
        else {
            folderPresent =1;
        }

        if(folderPresent==0){
            System.out.println("folder does not exist");
        }
        if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();

        List<String> command = List.of("mysql", "-u" + user, "-p" + pass);

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectInput(file);

            Process p = pb.start();
            int exitCode = p.waitFor();

            if (exitCode == 0) {
                JOptionPane.showMessageDialog(parent, "Restore Success!\nDatabase reverted to backup state.");
            }
            else {
                JOptionPane.showMessageDialog(parent, "Restore Failed(Exit Code: " + exitCode + ")");
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Restore Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}