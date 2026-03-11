package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;

import javax.swing.*;
import java.awt.*;

public class AddUser extends JDialog {

    private AdminService adminService;

    // core field
    private JTextField userField;
    private JPasswordField passField;
    private JComboBox<String> roleBox;

    // dynamic field stud
    private JTextField rollField;
    private JComboBox<String> programBox;

    // dyn flied inst
    private JTextField deptField;

    // container for dynami field
    private JPanel dynamicPanel;
    private CardLayout cardLayout;

    public AddUser(Frame owner) {
        super(owner, "Add New User", true);
        this.adminService = new AdminService();

        setSize(400, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        //panel
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        userField = new JTextField(15);
        topPanel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        passField = new JPasswordField(15);
        topPanel.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        topPanel.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        String[] roles = {"STUDENT", "INSTRUCTOR", "ADMIN"};
        roleBox = new JComboBox<>(roles);
        topPanel.add(roleBox, gbc);

        add(topPanel, BorderLayout.NORTH);

        // dynamic guard panel
        cardLayout = new CardLayout();
        dynamicPanel = new JPanel(cardLayout);
        dynamicPanel.setBorder(BorderFactory.createTitledBorder("Profile Details"));

        // stud view
        JPanel studentPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        studentPanel.add(new JLabel("Roll Number:"));
        rollField = new JTextField();
        studentPanel.add(rollField);

        studentPanel.add(new JLabel("Program:"));
        String[] programs = {"CSE", "CSAM", "CSAI", "CSD", "CSB", "ECE", "EVE", "CSEcon"};
        programBox = new JComboBox<>(programs);
        studentPanel.add(programBox);

        // inst view
        JPanel instPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        instPanel.add(new JLabel("Department:"));
        deptField = new JTextField();
        instPanel.add(deptField);

        // admin veiw
        JPanel adminPanel = new JPanel(new BorderLayout());
        adminPanel.add(new JLabel("No extra details needed for Admin.", SwingConstants.CENTER));

        // layout
        dynamicPanel.add(studentPanel, "STUDENT");
        dynamicPanel.add(instPanel, "INSTRUCTOR");
        dynamicPanel.add(adminPanel, "ADMIN");

        add(dynamicPanel, BorderLayout.CENTER);

        // bottom button
        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("Create User");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> handleSave());
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        add(btnPanel, BorderLayout.SOUTH);

        // switch guard
        roleBox.addActionListener(e -> {
            String selectedRole = (String) roleBox.getSelectedItem();
            cardLayout.show(dynamicPanel, selectedRole);
        });
    }

    private void handleSave() {
        String u = userField.getText().trim();
        String p = new String(passField.getPassword());
        String r = (String) roleBox.getSelectedItem();

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password are required.");
            return;
        }

        String extraInfo = "";

        // Prepare the extrainfo
        if ("STUDENT".equals(r)) {
            String roll = rollField.getText().trim();
            String prog = (String) programBox.getSelectedItem();

            if (roll.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Roll Number is required for Students.");
                return;
            }
            // Admin service understands it
            extraInfo = roll + "," + prog;

        } else if ("INSTRUCTOR".equals(r)) {
            String dept = deptField.getText().trim();
            if (dept.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Department is required for Instructors.");
                return;
            }
            extraInfo = dept;
        }

        // Call the servicess
        String result = adminService.registerUser(u, p, r, extraInfo);
        JOptionPane.showMessageDialog(this, result);

        if (result.startsWith("Success")) {
            dispose();
        }
    }
}