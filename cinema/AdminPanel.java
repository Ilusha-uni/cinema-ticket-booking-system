package cinema;
import cinema.panels.*;
import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JFrame {

    public AdminPanel() {
        // Set the window properties
        setTitle("Cinema Admin Panel");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center the window on the screen

        // Create a JTabbedPane to hold the tabs (each panel will be a tab)
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add tabs for each section of the admin panel
        //tabbedPane.addTab("Tickets", new TicketsPanel());
        //tabbedPane.addTab("Movies", new MoviesPanel());
        tabbedPane.addTab("Users", new UsersPanel());
       //tabbedPane.addTab("Halls", new HallsPanel());
        //tabbedPane.addTab("Screenings", new ScreeningsPanel());
        //tabbedPane.addTab("Seats", new SeatsPanel());

        // Add the tabbedPane to the frame
        add(tabbedPane, BorderLayout.CENTER);  // Main content in the center
    }

    public static void main(String[] args) {
        // Create and display the AdminPanel when the program starts
        SwingUtilities.invokeLater(() -> {
            AdminPanel adminPanel = new AdminPanel();
            adminPanel.setVisible(true);  // Make the window visible
        });
    }
}
