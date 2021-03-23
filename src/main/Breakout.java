package main;

import javax.swing.*;
import java.awt.*;

public class Breakout {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> {
                    JFrame mainFrame = new JFrame();
                    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    mainFrame.setTitle("Breakout");
                    mainFrame.setResizable(false);
                    mainFrame.add(new BreakoutPanel(), BorderLayout.CENTER);
                    mainFrame.pack();
                    mainFrame.setLocationRelativeTo(null);
                    mainFrame.setVisible(true);
                }
        );
    }
}
