package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import javax.swing.*;
import java.awt.*;

public class AddCourse extends JDialog {
    private AdminService service;
    private JTextField codeField, titleField, creditsField;

    public AddCourse(Frame owner) {
        super(owner, "Add one/more New Course(s):", true);
        this.service = new AdminService();
        setSize(350, 250);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Course Code(for ex:CS102):"));
        codeField = new JTextField();
        add(codeField);

        add(new JLabel("Titles:"));
        titleField = new JTextField();
        add(titleField);

        add(new JLabel("Credits:"));
        creditsField = new JTextField();
        add(creditsField);

        JButton saveBtn = new JButton("Save the Course");
        JButton cancelBtn = new JButton("Cancel It");

        saveBtn.addActionListener(e -> {
            String result = service.createCourse(codeField.getText(), titleField.getText(), creditsField.getText());
            JOptionPane.showMessageDialog(this, result);
            if(result.startsWith("Success!!!!")) dispose();
        });
        cancelBtn.addActionListener(e -> dispose());

        add(saveBtn);
        add(cancelBtn);
    }
}