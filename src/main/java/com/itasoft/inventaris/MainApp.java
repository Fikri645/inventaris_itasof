package com.itasoft.inventaris;

import com.itasoft.inventaris.ui.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    // Set Look and Feel ke sistem default untuk tampilan yang lebih baik
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    System.err.println("Tidak dapat mengatur Look and Feel: " + e.getMessage());
                }
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}