package edu.univ.erp.util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImageUtility {


    public static ImageIcon loadIcon(String filename, int width, int height) {
        //searching for image
        URL imgUrl = ImageUtility.class.getResource("/images/" + filename);

        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);

            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);

        }
        else {
            System.err.println("Could not find image: " + filename);
            return null;
        }
    }
}