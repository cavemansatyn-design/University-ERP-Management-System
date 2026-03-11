package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.CourseCatalog;
import edu.univ.erp.service.InstructorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import edu.univ.erp.ui.auth.LoginWindow;
import edu.univ.erp.util.ImageUtility;

public class InstructorDashboard extends JFrame {

    private String currentUser;
    private InstructorService instructorService;

    // Components
    private JTable sectionsTable;
    private DefaultTableModel sectionsModel;

    private JTable timetableTable;
    private DefaultTableModel timetableModel;

    public InstructorDashboard(String username) {
        ImageIcon appIcon = ImageUtility.loadIcon("logo.png", 64, 64);
        if (appIcon != null) {
            this.setIconImage(appIcon.getImage());
        }
        this.currentUser = username;
        this.instructorService = new InstructorService();

        setTitle("Instructor Dashboard - " + username);
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout()); // Use BorderLayout

        // --- TOP PANEL ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcomeLabel = new JLabel("Welcome, " + username + " (Instructor)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton signOutBtn = new JButton("Sign Out");
        signOutBtn.setBackground(Color.ORANGE);
        signOutBtn.addActionListener(e -> {
            this.dispose();
            new LoginWindow().setVisible(true);
        });

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(signOutBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- TABS ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("1. Manage Sections", createSectionsPanel());
        tabbedPane.addTab("2. My Timetable", createTimetablePanel());

        add(tabbedPane, BorderLayout.CENTER);
        refreshAll();
    }

    private void refreshAll() {
        loadSections();
        loadTimetable();
    }

    private JPanel createSectionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Removed "Time" from here as per your request style
        String[] cols = {"ID", "Code", "Title", "Room", "Capacity"};
        sectionsModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        sectionsTable = new JTable(sectionsModel);
        panel.add(new JScrollPane(sectionsTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton gradeBtn = new JButton("Open Gradebook");

        refreshBtn.addActionListener(e -> refreshAll());

        gradeBtn.addActionListener(e -> {
            int selectedRow = sectionsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a section first.");
                return;
            }
            // Column 0 is ID, Column 2 is Title
            int sectionId = (int) sectionsModel.getValueAt(selectedRow, 0);
            String title = (String) sectionsModel.getValueAt(selectedRow, 2);

            new GradebookWindow(sectionId, title).setVisible(true);
        });

        bottomPanel.add(refreshBtn);
        bottomPanel.add(gradeBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTimetablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"Day & Time", "Course", "Title", "Room"};
        timetableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        timetableTable = new JTable(timetableModel);
        timetableTable.setAutoCreateRowSorter(true); // Enable sorting

        panel.add(new JScrollPane(timetableTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh Timetable");
        refreshBtn.addActionListener(e -> loadTimetable());
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadSections() {
        sectionsModel.setRowCount(0);
        List<CourseCatalog> rows = instructorService.getSectionsForInstructor(currentUser);

        for (CourseCatalog row : rows) {
            sectionsModel.addRow(new Object[]{
                    row.getSectionId(),
                    row.getCourseCode(),
                    row.getCourseTitle(),
                    // row.getDayTime(), // Removed Time from main tab
                    row.getRoom(),
                    row.getCapacity()
            });
        }
    }

    private void loadTimetable() {
        timetableModel.setRowCount(0);
        List<CourseCatalog> rows = instructorService.getSectionsForInstructor(currentUser);

        for (CourseCatalog row : rows) {
            timetableModel.addRow(new Object[]{
                    row.getDayTime(), // Show time here
                    row.getCourseCode(),
                    row.getCourseTitle(),
                    row.getRoom()
            });
        }
    }
}