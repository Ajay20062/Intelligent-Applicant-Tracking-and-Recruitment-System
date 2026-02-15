package com.iats.app;

import com.iats.app.ui.WelcomeFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WelcomeFrame frame = new WelcomeFrame();
            frame.setVisible(true);
        });
    }
}
