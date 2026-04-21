// Package declaration
package cinema.panels;

// Import necessary libraries for GUI (Swing/AWT)
import javax.swing.*;
import java.awt.*;

/**
 * Represents the main window for cinema administrators.
 * It contains tabs for managing various parts of the system (e.g., Users, Movies, Showtimes).
 */
public class AdminPanel extends JFrame {

    /**
     * Constructs the AdminPanel window and sets up its components.
     */
    public AdminPanel() {
        // Set the window title
        setTitle("Cinema Admin Panel");
        // Set the window size
        setSize(1200, 700);
        // Ensure the application exits when this window is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Center the window on the screen
        setLocationRelativeTo(null);
        // Create a tabbed pane to hold different management sections
        JTabbedPane tabbedPane = new JTabbedPane();
        // Add a tab for managing users
        tabbedPane.addTab("Users", new UsersPanel());
                // Add the tabbed pane to the center of the frame
        add(tabbedPane, BorderLayout.CENTER);
    }
}
