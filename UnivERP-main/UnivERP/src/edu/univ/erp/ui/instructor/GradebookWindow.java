package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.EnrolledStudent;
import edu.univ.erp.domain.StudentScore;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.util.CsvUtility;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GradebookWindow extends JFrame {

    private int sectionId;
    private String courseTitle;
    private InstructorService instructorService;

    // --- Components for Tab 1 (Entry) ---
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> entryComponentBox;
    private JTextField scoreField;

    // --- Components for Tab 2 (View) ---
    private JTable scoreSheetTable;
    private DefaultTableModel scoreSheetModel;
    private JComboBox<String> viewComponentBox;

    public GradebookWindow(int sectionId, String courseTitle) {
        this.sectionId = sectionId;
        this.courseTitle = courseTitle;
        this.instructorService = new InstructorService();

        setTitle("Gradebook - " + courseTitle);
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Management & Data Entry
        tabbedPane.addTab("1. Enter Grades", createEntryPanel());

        // Tab 2: View Score Sheet
        tabbedPane.addTab("2. View Score Sheet", createViewPanel());

        add(tabbedPane);

        // Initial Loads
        loadStudents();
        refreshDropdowns();
    }

    // ================= TAB 1: ENTRY PANEL =================
    private JPanel createEntryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // --- TOP: Assessment Manager ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Manage Assessments"));

        JTextField newTestField = new JTextField(15);
        JButton addTestBtn = new JButton("Add New Assessment");

        addTestBtn.addActionListener(e -> {
            String name = newTestField.getText().trim();
            if (!name.isEmpty()) {
                String res = instructorService.createAssessment(sectionId, name);
                JOptionPane.showMessageDialog(this, res);
                newTestField.setText("");
                refreshDropdowns(); // Update both tabs
            }
        });

        topPanel.add(new JLabel("New Test Name:"));
        topPanel.add(newTestField);
        topPanel.add(addTestBtn);
        panel.add(topPanel, BorderLayout.NORTH);

        // --- CENTER: Student List ---
        String[] cols = {"Enrollment ID", "Roll No", "Name", "Program"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        studentTable = new JTable(tableModel);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // --- BOTTOM: Grading Form ---
        JPanel gradePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gradePanel.setBorder(BorderFactory.createTitledBorder("Enter Grade"));

        gradePanel.add(new JLabel("Select Assessment:"));
        entryComponentBox = new JComboBox<>();
        entryComponentBox.setPreferredSize(new Dimension(150, 25));
        gradePanel.add(entryComponentBox);

        gradePanel.add(new JLabel("Score:"));
        scoreField = new JTextField(5);
        gradePanel.add(scoreField);

        JButton saveBtn = new JButton("Save Grade");
        saveBtn.addActionListener(e -> handleSaveGrade());
        gradePanel.add(saveBtn);

        // --- NEW IMPORT BUTTON ---
        JButton importBtn = new JButton("Import from CSV");
        importBtn.setToolTipText("CSV Format: RollNo, Score (e.g. 2025001, 85)");
        importBtn.addActionListener(e -> handleImportCsv());
        gradePanel.add(importBtn);
        // -------------------------

        panel.add(gradePanel, BorderLayout.SOUTH);
        return panel;
    }

    // ================= TAB 2: VIEW PANEL =================
    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // --- TOP: Selector ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Assessment to View:"));

        viewComponentBox = new JComboBox<>();
        viewComponentBox.setPreferredSize(new Dimension(200, 25));
        topPanel.add(viewComponentBox);

        JButton loadBtn = new JButton("Load Scores");
        loadBtn.addActionListener(e -> loadScoreSheet());
        topPanel.add(loadBtn);
        // ---
        JButton exportBtn = new JButton("Export CSV");
        exportBtn.setToolTipText("Exports format: RollNo, Score (Ready for re-import)");

        exportBtn.addActionListener(e -> {
            if (scoreSheetTable.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No data to export.");
                return;
            }

            // 1. Prepare Headers
            String[] headers = {"RollNo", "Score"};

            // 2. Extract ONLY Roll No (Col 0) and Score (Col 2)
            // We skip Name (Col 1) to make it compatible with Import
            List<String[]> data = new java.util.ArrayList<>();

            for (int i = 0; i < scoreSheetModel.getRowCount(); i++) {
                String roll = (String) scoreSheetModel.getValueAt(i, 0);
                String scoreDisplay = (String) scoreSheetModel.getValueAt(i, 2);

                // Clean up the score:
                // If it says "Not Graded", make it empty "" so Excel is clean
                String cleanScore = "Not Graded".equals(scoreDisplay) ? "" : scoreDisplay;

                data.add(new String[]{roll, cleanScore});
            }

            // 3. Call the new Custom Export method
            CsvUtility.exportCustomData(this, data, headers, "Grades_For_Import.csv");
        });

        topPanel.add(exportBtn);
        // ---
        // Stats Button
        JButton avgBtn = new JButton("Show Average");
        avgBtn.addActionListener(e -> handleShowAverage());
        topPanel.add(avgBtn);

        panel.add(topPanel, BorderLayout.NORTH);

        // --- CENTER: Score Table ---
        String[] cols = {"Roll No", "Student Name", "Score / Grade"};
        scoreSheetModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        scoreSheetTable = new JTable(scoreSheetModel);
        // Make it readable
        scoreSheetTable.setRowHeight(25);
        scoreSheetTable.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(new JScrollPane(scoreSheetTable), BorderLayout.CENTER);


        return panel;
    }

    // ================= LOGIC =================

    private void loadStudents() {
        tableModel.setRowCount(0);
        List<EnrolledStudent> students = instructorService.getStudents(sectionId);
        for (EnrolledStudent stu : students) {
            tableModel.addRow(new Object[]{
                    stu.getEnrollmentId(), stu.getRollNo(), stu.getStudentName(), stu.getProgram()
            });
        }
    }

    private void refreshDropdowns() {
        entryComponentBox.removeAllItems();
        viewComponentBox.removeAllItems();

        List<String> tests = instructorService.getAssessmentList(sectionId);
        for (String t : tests) {
            entryComponentBox.addItem(t);
            viewComponentBox.addItem(t);
        }
    }

    private void handleSaveGrade() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a student in the table above.");
            return;
        }
        String component = (String) entryComponentBox.getSelectedItem();
        if (component == null) {
            JOptionPane.showMessageDialog(this, "No assessment selected.");
            return;
        }

        int enrollmentId = (int) tableModel.getValueAt(selectedRow, 0);
        String scoreStr = scoreField.getText().trim();

        String result = instructorService.submitGrade(enrollmentId, component, scoreStr);
        JOptionPane.showMessageDialog(this, result);

        if (result.startsWith("Success")) {
            scoreField.setText("");
            // Optional: Automatically refresh the view tab if needed
        }
    }

    private void loadScoreSheet() {
        String component = (String) viewComponentBox.getSelectedItem();
        if (component == null) return;

        scoreSheetModel.setRowCount(0);
        List<StudentScore> rows = instructorService.getScoreSheet(sectionId, component);

        for (StudentScore row : rows) {
            scoreSheetModel.addRow(new Object[]{
                    row.getRollNo(),
                    row.getStudentName(),
                    row.getScoreDisplay() // Shows "85.0" or "Not Graded"
            });
        }
    }

    private void handleShowAverage() {
        String component = (String) viewComponentBox.getSelectedItem();
        if (component == null) return;
        String result = instructorService.getClassAverage(sectionId, component);
        JOptionPane.showMessageDialog(this, result);
    }

    private void handleImportCsv() {
        String component = (String) entryComponentBox.getSelectedItem();
        if (component == null) {
            JOptionPane.showMessageDialog(this, "Please select an Assessment first.");
            return;
        }

        List<String[]> rows = CsvUtility.readCsvFile(this);
        if (rows == null) return; // User cancelled or error

        int successCount = 0;
        int failCount = 0;
        StringBuilder errors = new StringBuilder();

        // We need to map RollNo -> EnrollmentId to save grades
        // Let's get the current student list from the table logic
        List<EnrolledStudent> students = instructorService.getStudents(sectionId);

        for (String[] row : rows) {
            if (row.length < 2) continue; // Skip bad lines

            // Expected format: RollNo, Score
            String csvRollNo = row[0];
            String csvScore = row[1];

            // 1. Find the Enrollment ID for this RollNo
            int enrollmentId = -1;
            for (EnrolledStudent s : students) {
                if (s.getRollNo().equalsIgnoreCase(csvRollNo)) {
                    enrollmentId = s.getEnrollmentId();
                    break;
                }
            }

            if (enrollmentId != -1) {
                // 2. Submit Grade
                String result = instructorService.submitGrade(enrollmentId, component, csvScore);
                if (result.startsWith("Success")) successCount++;
                else {
                    failCount++;
                    errors.append("Roll ").append(csvRollNo).append(": ").append(result).append("\n");
                }
            } else {
                failCount++;
                errors.append("Roll ").append(csvRollNo).append(": Student not found in this class.\n");
            }
        }

        String msg = "Import Complete.\nSuccess: " + successCount + "\nFailed: " + failCount;
        if (failCount > 0) msg += "\n\nErrors:\n" + errors.toString();

        JOptionPane.showMessageDialog(this, msg);
    }
}