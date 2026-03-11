package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ResetPassword extends JDialog {

    private AdminService service;

    // Data
    private List<String> allUsers;
    private DefaultListModel<String> listModel;

    // UIComponents
    private JTextField searchField;
    private JList<String> userList;
    private JPasswordField newPassField;

    public ResetPassword(Frame owner) {
        super(owner, "Admin Reset Password", true);
        this.service = new AdminService();

        setSize(400, 500); // Taller to fit list
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // top search bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        topPanel.add(new JLabel("1. Search & Select User:"), BorderLayout.NORTH);

        searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterList(); }
            public void removeUpdate(DocumentEvent e) { filterList(); }
            public void changedUpdate(DocumentEvent e) { filterList(); }
        });

        topPanel.add(searchField, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // center user list
        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // bottom panel
        JPanel bottomContainer = new JPanel(new GridLayout(2, 1, 5, 5));
        bottomContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // pass row
        JPanel passPanel = new JPanel(new BorderLayout(10, 0));
        passPanel.add(new JLabel("2. New Password:"), BorderLayout.WEST);
        newPassField = new JPasswordField();
        passPanel.add(newPassField, BorderLayout.CENTER);

        // buttton row
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton resetBtn = new JButton("Reset Password");
        resetBtn.setForeground(Color.RED);

        JButton cancelBtn = new JButton("Cancel");

        resetBtn.addActionListener(e -> handleReset());
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(resetBtn);
        btnPanel.add(cancelBtn);

        bottomContainer.add(passPanel);
        bottomContainer.add(btnPanel);

        add(bottomContainer, BorderLayout.SOUTH);

        // Loada dta
        loadAllUsers();
    }

    private void loadAllUsers() {
        allUsers = service.getAllUsers();
        updateListUI(allUsers);
    }

    private void filterList() {
        String query = searchField.getText().trim().toLowerCase();
        List<String> filtered = allUsers.stream()
                .filter(name -> name.toLowerCase().contains(query))
                .collect(Collectors.toList());
        updateListUI(filtered);
    }

    private void updateListUI(List<String> usersToShow) {
        listModel.clear();
        for (String u : usersToShow) listModel.addElement(u);
    }

    private void handleReset() {
        String selectedUser = userList.getSelectedValue();
        String newPass = new String(newPassField.getPassword());

        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user from the list.");
            return;
        }
        if (newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a new password.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Overwrite password for user '" + selectedUser + "'?",
                "Confirm Reset", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String result = service.resetUserPassword(selectedUser, newPass);
            JOptionPane.showMessageDialog(this, result);
            if (result.startsWith("Success")) dispose();
        }
    }
}