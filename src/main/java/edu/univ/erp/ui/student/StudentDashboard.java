package edu.univ.erp.ui.student;

import edu.univ.erp.domain.CourseCatalog;
import edu.univ.erp.domain.StudentGrade;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.auth.ChangePassword;
import edu.univ.erp.ui.auth.LoginWindow;
import edu.univ.erp.util.CsvUtility;
import edu.univ.erp.util.ImageUtility;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {

    private StudentService studentService;
    private String currentUser;

    // --- Components ---
    private JTable catalogTable;
    private DefaultTableModel catalogModel;

    private JTable myTable;
    private DefaultTableModel myModel;

    private JTable timeTable;
    private DefaultTableModel timeModel;

    public StudentDashboard(String username) {
        ImageIcon appIcon = ImageUtility.loadIcon("logo.png", 64, 64);
        if (appIcon != null) {
            this.setIconImage(appIcon.getImage());
        }
        this.currentUser = username;
        this.studentService = new StudentService();

        setTitle("Student Dashboard - " + username);
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Use BorderLayout for the main window
        setLayout(new BorderLayout());

        // --- 1. TOP PANEL (User Info + Buttons) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcomeLabel = new JLabel("Welcome, " + username + " (Student)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Button Container
        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Notification Button
        JButton notifBtn = new JButton("🔔 Notifications");
        notifBtn.addActionListener(e -> showNotifications());

        // Change Password Button
        JButton changePassBtn = new JButton("Change Password");
        changePassBtn.addActionListener(e -> new ChangePassword(this, currentUser).setVisible(true));

        // Sign Out Button
        JButton signOutBtn = new JButton("Sign Out");
        signOutBtn.setBackground(Color.ORANGE);
        signOutBtn.addActionListener(e -> logout());

        topButtons.add(notifBtn);
        topButtons.add(changePassBtn);
        topButtons.add(signOutBtn);

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(topButtons, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // --- 2. MAIN TABS ---
        JTabbedPane tabbedPane = new JTabbedPane();

        // TAB 1: My Courses (Was Manage Registrations)
        tabbedPane.addTab("1. My Courses", createMyCoursesPanel());

        // TAB 2: Course Catalog
        tabbedPane.addTab("2. Course Catalog", createCatalogPanel());

        // TAB 3: Timetable
        tabbedPane.addTab("3. My Timetable", createTimetablePanel());

        add(tabbedPane, BorderLayout.CENTER);

        refreshAll();
    }

    private void logout() {
        this.dispose();
        new LoginWindow().setVisible(true);
    }

    private void refreshAll() {
        loadCatalogData();
        loadMyCourses();
        loadTimetable();
    }

    // --- TAB 1: MY COURSES PANEL (Renamed from Manage Registrations) ---
    // --- TAB 1: MY COURSES PANEL ---
    private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"ID", "Code", "Title", "Instructor", "Room"};

        myModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        myTable = new JTable(myModel);
        panel.add(new JScrollPane(myTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();

        // 1. View Grades
        JButton gradesBtn = new JButton("View Grades");
        gradesBtn.addActionListener(e -> handleViewGrades());

        // 2. NEW: Export Transcript Button
        JButton exportBtn = new JButton("Export Transcript");
        exportBtn.addActionListener(e -> {
            if (myTable.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No courses to export.");
                return;
            }
            // Uses the generic table export since we just want a simple list
            CsvUtility.exportTable(this, myTable, "My_Transcript.csv");
        });

        // 3. Drop Button
        JButton dropBtn = new JButton("Drop Selected");
        dropBtn.addActionListener(e -> handleDrop());

        // 4. Refresh Button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshAll());

        btnPanel.add(gradesBtn);
        btnPanel.add(exportBtn); // Add it here
        btnPanel.add(dropBtn);
        btnPanel.add(refreshBtn);

        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // --- TAB 2: CATALOG PANEL ---
    private JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // No Time column here
        String[] cols = {"ID", "Code", "Title", "Instructor", "Credits", "Room", "Seats"};

        catalogModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        catalogTable = new JTable(catalogModel);
        panel.add(new JScrollPane(catalogTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton registerBtn = new JButton("Register Selected");
        JButton refreshBtn = new JButton("Refresh");

        registerBtn.addActionListener(e -> handleRegister());
        refreshBtn.addActionListener(e -> refreshAll());

        btnPanel.add(registerBtn);
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // --- TAB 3: TIMETABLE PANEL ---
    private JPanel createTimetablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"Day & Time", "Course", "Title", "Room", "Instructor"};

        timeModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        timeTable = new JTable(timeModel);
        timeTable.setAutoCreateRowSorter(true);

        panel.add(new JScrollPane(timeTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh Timetable");
        refreshBtn.addActionListener(e -> refreshAll());

        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // --- DATA LOADING ---

    private void loadCatalogData() {
        catalogModel.setRowCount(0);
        List<CourseCatalog> rows = studentService.getCatalog();
        for (CourseCatalog row : rows) {
            catalogModel.addRow(new Object[]{
                    row.getSectionId(), row.getCourseCode(), row.getCourseTitle(),
                    row.getInstructorName(), row.getCredits(), row.getRoom(), row.getCapacity()
            });
        }
    }

    private void loadMyCourses() {
        myModel.setRowCount(0);
        List<CourseCatalog> rows = studentService.getMyRegistrations(currentUser);
        for (CourseCatalog row : rows) {
            myModel.addRow(new Object[]{
                    row.getSectionId(), row.getCourseCode(), row.getCourseTitle(),
                    row.getInstructorName(), row.getRoom()
            });
        }
    }

    private void loadTimetable() {
        timeModel.setRowCount(0);
        List<CourseCatalog> rows = studentService.getMyRegistrations(currentUser);
        for (CourseCatalog row : rows) {
            timeModel.addRow(new Object[]{
                    row.getDayTime(), row.getCourseCode(), row.getCourseTitle(),
                    row.getRoom(), row.getInstructorName()
            });
        }
    }

    // --- ACTIONS ---

    private void handleRegister() {
        int selectedRow = catalogTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to register.");
            return;
        }
        int modelRow = catalogTable.convertRowIndexToModel(selectedRow);
        int sectionId = (int) catalogModel.getValueAt(modelRow, 0);
        int capacity = (int) catalogModel.getValueAt(modelRow, 6);

        String result = studentService.register(currentUser, sectionId, capacity);
        JOptionPane.showMessageDialog(this, result);
        refreshAll();
    }

    private void handleDrop() {
        int selectedRow = myTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to drop.");
            return;
        }
        int modelRow = myTable.convertRowIndexToModel(selectedRow);
        int sectionId = (int) myModel.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Drop this course?");
        if (confirm == JOptionPane.YES_OPTION) {
            String result = studentService.dropCourse(currentUser, sectionId);
            JOptionPane.showMessageDialog(this, result);
            refreshAll();
        }
    }

    private void handleViewGrades() {
        int selectedRow = myTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to view grades.");
            return;
        }

        int modelRow = myTable.convertRowIndexToModel(selectedRow);
        int sectionId = (int) myModel.getValueAt(modelRow, 0);
        String courseTitle = (String) myModel.getValueAt(modelRow, 2);

        // Fetch data
        List<StudentGrade> grades = studentService.viewGrades(currentUser, sectionId);

        if (grades.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No assessments defined for this course yet.");
            return;
        }

        // Build a simple table for the popup
        String[] cols = {"Component", "My Score", "Class Average"};
        DefaultTableModel gradeModel = new DefaultTableModel(cols, 0);

        for (StudentGrade row : grades) {
            gradeModel.addRow(new Object[]{
                    row.getComponent(),
                    row.getMyScoreDisplay(),
                    row.getClassAverageDisplay()
            });
        }

        JTable gradeTable = new JTable(gradeModel);
        gradeTable.setEnabled(false); // Read-only

        JScrollPane scroll = new JScrollPane(gradeTable);
        scroll.setPreferredSize(new Dimension(400, 200));

        JOptionPane.showMessageDialog(this, scroll, "Grades for " + courseTitle, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showNotifications() {
        List<String> msgs = studentService.getNotifications(currentUser);

        if (msgs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No notifications.");
        } else {
            JList<String> list = new JList<>(msgs.toArray(new String[0]));
            JScrollPane scroll = new JScrollPane(list);
            scroll.setPreferredSize(new Dimension(350, 200));

            JOptionPane.showMessageDialog(this, scroll, "My Notifications", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}