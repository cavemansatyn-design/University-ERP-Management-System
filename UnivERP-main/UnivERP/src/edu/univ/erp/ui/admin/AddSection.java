package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

//helper class for dropp downns menus
class ComboItem {
    private String label;
    private int id;
    public ComboItem(int id, String label) { this.id = id; this.label = label; }
    public int getId() { return id; }
    public String toString() { return label; }
}

public class AddSection extends JDialog {
    private AdminService service;
    private JComboBox<ComboItem> courseBox;
    private JComboBox<ComboItem> instructorBox;

    // UI COMPONENNT for time selec
    private JCheckBox monBox, tueBox, wedBox, thuBox, friBox;
    private JComboBox<String> startTimeBox;
    private JComboBox<String> endTimeBox;

    private JTextField roomField, capField;

    public AddSection(Frame owner) {
        super(owner, "Create Section and also Assign the Instructor", true);
        this.service = new AdminService();
        setSize(500, 450); // window big ho gaya
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1course dropdown
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Select a Course:"), gbc);

        courseBox = new JComboBox<>();
        loadCourses();
        gbc.gridx = 1;
        add(courseBox, gbc);

        // 2instructor droppdown
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Assign the Instructor:"), gbc);

        instructorBox = new JComboBox<>();
        loadInstructors();
        gbc.gridx = 1;
        add(instructorBox, gbc);

        // 3. day select (multiple checkbo)
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Select the Days:"), gbc);

        JPanel dayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        monBox = new JCheckBox("Mond");
        tueBox = new JCheckBox("Tues");
        wedBox = new JCheckBox("Wedn");
        thuBox = new JCheckBox("Thur");
        friBox = new JCheckBox("Frid");

        dayPanel.add(monBox);
        dayPanel.add(tueBox);
        dayPanel.add(wedBox);
        dayPanel.add(thuBox);
        dayPanel.add(friBox);

        gbc.gridx = 1;
        add(dayPanel, gbc);

        // 4time Select(Dropdowns)
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Time Slots:"), gbc);

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] times = {"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"};
        startTimeBox = new JComboBox<>(times);
        endTimeBox = new JComboBox<>(times);
        endTimeBox.setSelectedIndex(1); // default one hr later

        timePanel.add(startTimeBox);
        timePanel.add(new JLabel("to"));
        timePanel.add(endTimeBox);

        gbc.gridx = 1;
        add(timePanel, gbc);

        //Room & Capac
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Room:"), gbc);
        roomField = new JTextField();
        gbc.gridx = 1;
        add(roomField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        add(new JLabel("Capacity:"), gbc);
        capField = new JTextField();
        gbc.gridx = 1;
        add(capField, gbc);

        // buttonss
        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("Create Sections");
        JButton cancelBtn = new JButton("Cancell");

        saveBtn.addActionListener(e -> handleSave());
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(btnPanel, gbc);
    }

    private void loadCourses() {
        Map<Integer, String> map = service.getCourseList();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            courseBox.addItem(new ComboItem(entry.getKey(), entry.getValue()));
        }
    }

    private void loadInstructors() {
        Map<Integer, String> map = service.getInstructorList();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            instructorBox.addItem(new ComboItem(entry.getKey(), entry.getValue()));
        }
    }

    private void handleSave() {
        ComboItem selectedCourse = (ComboItem) courseBox.getSelectedItem();
        ComboItem selectedInst = (ComboItem) instructorBox.getSelectedItem();

        if (selectedCourse == null || selectedInst == null) {
            JOptionPane.showMessageDialog(this, "Please select Course and the  Instructor!");
            return;
        }

        // Build the daytime String
        List<String> days = new ArrayList<>();
        if (monBox.isSelected()) days.add("Mond");
        if (tueBox.isSelected()) days.add("Tues");
        if (wedBox.isSelected()) days.add("Wedn");
        if (thuBox.isSelected()) days.add("Thur");
        if (friBox.isSelected()) days.add("Frid");

        if (days.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one day!");
            return;
        }

        String dayStr = String.join(",", days); // eg "Mon,Wed"
        String timeStr = startTimeBox.getSelectedItem() + "-" + endTimeBox.getSelectedItem();

        //"Mon,Wed 09:00-10:00"
        String finalDayTime = dayStr + " " + timeStr;

        String result = service.createSection(
                selectedCourse.getId(),
                selectedInst.getId(),
                finalDayTime,
                roomField.getText(),
                capField.getText()
        );
        JOptionPane.showMessageDialog(this, result);
        if (result.startsWith("Success")) dispose();
    }
}