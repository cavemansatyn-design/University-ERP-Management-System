package edu.univ.erp.ui.auth;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.ChangePassword;
import edu.univ.erp.ui.admin.AdminDashboard;
import edu.univ.erp.ui.instructor.InstructorDashboard;
import edu.univ.erp.ui.student.StudentDashboard;
import edu.univ.erp.util.ImageUtility; // Import the helper

import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class LoginWindow extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final AuthService authService;

    public LoginWindow() {
        this.authService = new AuthService();

        setTitle("University ERP - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 380); // Increased height to fit the logo
        setLocationRelativeTo(null);

        // --- SET WINDOW ICON (Taskbar) ---
        ImageIcon appIcon = ImageUtility.loadIcon("logo.png", 64, 64);
        if (appIcon != null) {
            this.setIconImage(appIcon.getImage());
        }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8); // Tighter vertical padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- 1. LOGO (Top Center) ---
        // Row 0
        ImageIcon logoIcon = ImageUtility.loadIcon("logo.png", 120, 70);
        if (logoIcon != null) {
            JLabel logoLabel = new JLabel(logoIcon);
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(10, 0, 5, 0);
            panel.add(logoLabel, gbc);
        }

        // --- 2. Header ---
        // Row 1
        JLabel title = new JLabel("Welcome", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        // --- 3. Username ---
        // Row 2
        gbc.gridwidth = 1; // Reset to 1 column
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        usernameField = new JTextField(20);
        panel.add(usernameField, gbc);

        // --- 4. Password ---
        // Row 3
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        // --- 5. Login Buttons ---
        // Row 4
        JPanel mainBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Sign In");
        JButton cancelButton = new JButton("Exit");

        loginButton.addActionListener(e -> handleLogin());
        cancelButton.addActionListener(e -> System.exit(0));

        mainBtnPanel.add(loginButton);
        mainBtnPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(mainBtnPanel, gbc);

        // --- 6. Dark Mode Toggle ---
        // Row 5 (Left)
        JCheckBox darkModeBox = new JCheckBox("Dark Mode");
        darkModeBox.setFocusable(false);

        darkModeBox.addActionListener(e -> {
            try {
                if (darkModeBox.isSelected()) {
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                } else {
                    UIManager.setLookAndFeel(new FlatLightLaf());
                }
                SwingUtilities.updateComponentTreeUI(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 1;
        panel.add(darkModeBox, gbc);

        // --- 7. Change Password Link ---
        // Row 5 (Right)
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        JButton changePassBtn = new JButton("Change Password");
        changePassBtn.setForeground(Color.BLUE);
        changePassBtn.setBorderPainted(false);
        changePassBtn.setContentAreaFilled(false);

        changePassBtn.addActionListener(e -> {
            String currentText = usernameField.getText().trim();
            new ChangePassword(this, currentText).setVisible(true);
        });

        panel.add(changePassBtn, gbc);

        add(panel);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        String result = authService.login(username, password);

        // 1. Check for Warning
        if ("FAILURE_WARNING".equals(result)) {
            JOptionPane.showMessageDialog(this,
                    "Incorrect password.\nWARNING: You have failed 5 times.",
                    "Login Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Success Logic
        if (result != null) {
            // --- FIX START: Capture Window State ---
            // getting the state (Normal vs Maximized) BEFORE we close the window
            int currentWindowState = this.getExtendedState();
            // ---------------------------------------

            this.dispose(); // Close Login Window

            JFrame nextDashboard = null;

            // Create the correct dashboard object
            switch (result) {
                case "ADMIN":
                    nextDashboard = new AdminDashboard();
                    break;
                case "INSTRUCTOR":
                    nextDashboard = new InstructorDashboard(username);
                    break;
                case "STUDENT":
                    nextDashboard = new StudentDashboard(username);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Unknown role: " + result);
                    return;
            }

            if (nextDashboard != null) {
                // --- FIX END: Apply Window State ---
                // Apply the captured state to the new window
                nextDashboard.setExtendedState(currentWindowState);
                nextDashboard.setVisible(true);
            }

        } else {
            JOptionPane.showMessageDialog(this,
                    "Incorrect username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}