package edu.univ.erp.auth;

import edu.univ.erp.auth.AuthService;
import javax.swing.*;
import java.awt.*;

public class ChangePassword extends JDialog {

    private AuthService authService;
    int small = 0;
    // UI Components
    private JTextField userField;
    private JPasswordField oldPassField;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;

    public ChangePassword(Frame topFrame, String Uname) {

        super(topFrame, "Change Password", true);
        this.authService = new AuthService();
        int width;
        int height;
        if(small == 0)
        {
            width = 400;
            height = 300;
        }
        else{
            width = 600;
            height = 400;
        }
        setSize(width, height);

        setLocationRelativeTo(topFrame);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //username Field
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);

        userField = new JTextField(15);
        userField.setText(Uname);
        gbc.gridx = 1;
        add(userField, gbc);

        //old Password
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Old Password:"), gbc);

        oldPassField = new JPasswordField(15);
        gbc.gridx = 1;
        add(oldPassField, gbc);

        //new Password
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("New Password:"), gbc);

        newPassField = new JPasswordField(15);
        gbc.gridx = 1;
        add(newPassField, gbc);

        //confirm Password
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Confirm New:"), gbc);

        confirmPassField = new JPasswordField(15);
        gbc.gridx = 1;
        add(confirmPassField, gbc);

        //buttons
        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("Change Password");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> handleChange());
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(btnPanel, gbc);
    }

    private void handleChange() {
        //read username
        String username = userField.getText().trim();
        String oldPass = new String(oldPassField.getPassword());
        String newPass = new String(newPassField.getPassword());
        String confPass = new String(confirmPassField.getPassword());


        if (!newPass.equals(confPass)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.");
            return;
        }
        if (username.isEmpty() || oldPass.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        if (authService.changePassword(username, oldPass, newPass)) {
            JOptionPane.showMessageDialog(this, "Success! Password changed.");
            dispose();
        }
        else {
            JOptionPane.showMessageDialog(this, "Error: Username or Old Password incorrect.");
        }
    }
}