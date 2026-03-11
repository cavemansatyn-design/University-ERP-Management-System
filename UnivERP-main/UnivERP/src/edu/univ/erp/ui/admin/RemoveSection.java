package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class RemoveSection extends JDialog {
    private AdminService service;
    private JComboBox<ComboItem> sectionBox;

    public RemoveSection(Frame owner) {
        super(owner, "Remove Section", true);
        this.service = new AdminService();
        setSize(450, 180); // wide wind
        setLocationRelativeTo(owner);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        add(new JLabel("Select Section to Delete:"));

        sectionBox = new JComboBox<>();
        // dropdown wide
        sectionBox.setPreferredSize(new Dimension(350, 30));
        loadSections();
        add(sectionBox);

        JButton deleteBtn = new JButton("Delete Section");
        deleteBtn.setBackground(Color.RED);
        deleteBtn.setForeground(Color.WHITE);

        deleteBtn.addActionListener(e -> handleDelete());
        add(deleteBtn);
    }

    private void loadSections() {
        Map<Integer, String> map = service.getSectionList();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            sectionBox.addItem(new ComboItem(entry.getKey(), entry.getValue()));
        }
    }

    private void handleDelete() {
        ComboItem selected = (ComboItem) sectionBox.getSelectedItem();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "WARNING: Deleting " + selected + "\n" +
                        "will also remove ALL student enrollments for this class.\n\n" +
                        "Are you sure?",
                "Confirm Dangerous Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String result = service.removeSection(selected.getId());
            JOptionPane.showMessageDialog(this, result);
            if (result.startsWith("Success")) dispose();
        }
    }
}