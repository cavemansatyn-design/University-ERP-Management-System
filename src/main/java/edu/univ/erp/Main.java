package edu.univ.erp;

import com.formdev.flatlaf.FlatLightLaf;
import edu.univ.erp.ui.auth.LoginWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {

        //flatlaf setup
        try {

            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Flatlaf error");
        }

        //run app
        SwingUtilities.invokeLater(() -> {
            LoginWindow login = new LoginWindow();
            login.setVisible(true);
        });
    }
}