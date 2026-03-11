package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class RemoveCourse extends JDialog {
    private AdminService service;
    private JComboBox<ComboItem> courseBox;

    public RemoveCourse(Frame owner) {
        super(owner, "Remove Course", true);
        this.service = new AdminService();
        setSize(350, 150);
        setLocationRelativeTo(owner);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        add(new JLabel("Select Course to Delete:"));

        courseBox = new JComboBox<>();
        loadCourses();
        add(courseBox);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(Color.RED); // red danger
        deleteBtn.setForeground(Color.WHITE);

        deleteBtn.addActionListener(e -> handleDelete());
        add(deleteBtn);
    }

    private void loadCourses() {
        Map<Integer, String> map = service.getCourseList();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            courseBox.addItem(new ComboItem(entry.getKey(), entry.getValue()));
        }
    }

    private void handleDelete() {
        ComboItem selected = (ComboItem) courseBox.getSelectedItem();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + selected + "?\nThis cannot be undone.",
                "Confirm the Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String result = service.removeCourse(selected.getId());
            JOptionPane.showMessageDialog(this, result);
            if (result.startsWith("Success!")) dispose();
        }
    }
}