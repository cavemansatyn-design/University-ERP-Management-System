package edu.univ.erp.ui.admin;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.ui.auth.LoginWindow;
import edu.univ.erp.util.BackupUtility;
import edu.univ.erp.util.ImageUtility;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private AccessControl accessControl;
    private JLabel statusLabel;

    public AdminDashboard() {
        ImageIcon appIcon = ImageUtility.loadIcon("logo.png", 64, 64);
        if (appIcon != null) {
            this.setIconImage(appIcon.getImage());
        }
        this.accessControl = new AccessControl();

        setTitle("Admin Dashboard");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // header
        JLabel title = new JLabel("System Administration", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        //center grid
        JPanel gridPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        // 1status label
        statusLabel = new JLabel("Checking status!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setOpaque(true);
        gridPanel.add(statusLabel);

        // updated secct sta here
        // 2. User Management (Split row for Add User & Reset Password)
        JPanel userPanel = new JPanel(new GridLayout(1, 2, 5, 0));

        JButton addUserBtn = new JButton("Add User");
        addUserBtn.addActionListener(e -> new AddUser(this).setVisible(true));

        JButton delUserBtn = new JButton("Remove User");
        delUserBtn.addActionListener(e -> new RemoveUser(this).setVisible(true));

        JButton resetPassBtn = new JButton("Reset Password");
        resetPassBtn.addActionListener(e -> new ResetPassword(this).setVisible(true));

        userPanel.add(addUserBtn);
        userPanel.add(delUserBtn);
        userPanel.add(resetPassBtn);
        gridPanel.add(userPanel);
        //end here

        // creat course
        JPanel coursePanel = new JPanel(new GridLayout(1, 2, 5, 0));
        JButton addCourseBtn = new JButton("Create Course");
        addCourseBtn.addActionListener(e -> new AddCourse(this).setVisible(true));

        JButton delCourseBtn = new JButton("Remove Course");
        delCourseBtn.addActionListener(e -> new RemoveCourse(this).setVisible(true));

        coursePanel.add(addCourseBtn);
        coursePanel.add(delCourseBtn);
        gridPanel.add(coursePanel);

        // create/remove sec
        JPanel sectionPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        JButton addSectionBtn = new JButton("Create Section");
        addSectionBtn.addActionListener(e -> new AddSection(this).setVisible(true));

        JButton delSectionBtn = new JButton("Remove Section");
        delSectionBtn.addActionListener(e -> new RemoveSection(this).setVisible(true));

        sectionPanel.add(addSectionBtn);
        sectionPanel.add(delSectionBtn);
        gridPanel.add(sectionPanel);

        // mkamintanence
        JButton maintBtn = new JButton("Toggle Maintenance Mode");
        maintBtn.addActionListener(e -> toggleMaintenance());
        gridPanel.add(maintBtn);

        add(gridPanel, BorderLayout.CENTER);


        // backup restore
        JPanel backupPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        JButton backupBtn = new JButton("Backup DB");
        backupBtn.addActionListener(e -> BackupUtility.backup(this));

        JButton restoreBtn = new JButton("Restore DB");
        restoreBtn.addActionListener(e -> BackupUtility.restore(this));

        backupPanel.add(backupBtn);
        backupPanel.add(restoreBtn);
        gridPanel.add(backupPanel);

        //grid row change
        gridPanel.setLayout(new GridLayout(6, 1, 10, 10));

        // sign out
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton signOutBtn = new JButton("Sign Out");
        signOutBtn.setBackground(Color.ORANGE);
        signOutBtn.setPreferredSize(new Dimension(150, 30));
        signOutBtn.addActionListener(e -> {
            dispose();
            new LoginWindow().setVisible(true);
        });

        bottomPanel.add(signOutBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Initiall statu
        updateStatusDisplay();
    }

    private void updateStatusDisplay() {
        boolean isMaintenance = accessControl.isMaintenanceMode();
        if (isMaintenance) {
            statusLabel.setText("STATUS: MAINTENANCE MODE (ON)");
            statusLabel.setBackground(Color.RED);
            statusLabel.setForeground(Color.WHITE);
        } else {
            statusLabel.setText("STATUS: ACTIVE (Normal)");
            statusLabel.setBackground(Color.GREEN);
            statusLabel.setForeground(Color.BLACK);
        }
    }

    private void toggleMaintenance() {
        boolean current = accessControl.isMaintenanceMode();
        if (accessControl.setMaintenanceMode(!current)) {
            updateStatusDisplay();
            JOptionPane.showMessageDialog(this, "Maintenance Mode updated.");
        } else {
            JOptionPane.showMessageDialog(this, "Error updating settings.");
        }
    }
}