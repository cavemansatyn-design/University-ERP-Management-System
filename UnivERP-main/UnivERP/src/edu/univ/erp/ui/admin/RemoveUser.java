package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List; // Import standard List
import java.util.stream.Collectors;

public class RemoveUser extends JDialog {

    private AdminService service;

    private List<String> allUsers; // full list in memory
    private DefaultListModel<String> listModel; // jlist ui model

    //UI comp
    private JTextField searchField;
    private JList<String> userList;

    public RemoveUser(Frame owner) {
        super(owner, "Remove the User", true);
        this.service = new AdminService();

        setSize(400, 400); // bigger wind
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        topPanel.add(new JLabel("Search User:"), BorderLayout.NORTH);

        searchField = new JTextField();
        // Alistener to filtr
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterList(); }
            public void removeUpdate(DocumentEvent e) { filterList(); }
            public void changedUpdate(DocumentEvent e) { filterList(); }
        });

        topPanel.add(searchField, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // centr panel
        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Select User to Delete"));

        //pad
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // bottom anel
        JPanel btnPanel = new JPanel();
        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setBackground(Color.RED);
        deleteBtn.setForeground(Color.WHITE);

        JButton cancelBtn = new JButton("Cancel");

        deleteBtn.addActionListener(e -> handleDelete());
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(deleteBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // loada data
        loadAllUsers();
    }

    private void loadAllUsers() {
        // Fetch all users from DB
        allUsers = service.getAllUsers();
        // Show them all initiallyy
        updateListUI(allUsers);
    }

    private void filterList() {
        String query = searchField.getText().trim().toLowerCase();

        // Filter the master lists
        List<String> filtered = allUsers.stream()
                .filter(name -> name.toLowerCase().contains(query))
                .collect(Collectors.toList());

        updateListUI(filtered);
    }

    private void updateListUI(List<String> usersToShow) {
        listModel.clear();
        for (String u : usersToShow) {
            listModel.addElement(u);
        }
    }

    private void handleDelete() {
        String selectedUser = userList.getSelectedValue();

        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please click on a user in the list to select them.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user '" + selectedUser + "'?\n" +
                        "This will delete their profile, grades, and enrollments.\n" +
                        "This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String result = service.removeUser(selectedUser);
            JOptionPane.showMessageDialog(this, result);

            if (result.startsWith("Success")) {
                // Remove from memory and UI immediately so they don't see it anymore
                allUsers.remove(selectedUser);
                filterList(); // Refresh view
            }
        }
    }
}