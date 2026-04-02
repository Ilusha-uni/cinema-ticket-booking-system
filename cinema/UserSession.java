package cinema;

public class UserSession {
    private static int loggedInUserId = -1;  // Default to -1 (no user logged in)

    // Get the logged-in user's ID
    public static int getUserId() {
        // Fetch and return the logged-in user’s ID
        // For example, if you're storing it in a static field or session:
        return loggedInUserId; // Replace with actual logic to get logged-in user
    }

    // Set the logged-in user's ID
    public static void setUserId(int userId) {
        loggedInUserId = userId;
    }

    // Check if a user is logged in
    public static boolean isLoggedIn() {
        return loggedInUserId != -1;
    }

    // Logout the user (clear the session)
    public static void logout() {
        loggedInUserId = -1;
    }
}