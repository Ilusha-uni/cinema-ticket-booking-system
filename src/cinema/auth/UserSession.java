package cinema.auth; // Package declaration

/**
 * Manages the currently logged-in user's session.
 * Stores the user ID and provides methods to query and modify session state.
 */
public class UserSession {
    /** Stores the ID of the currently logged-in user; -1 if no user is logged in. */
    private static int loggedInUserId = -1;

    /**
     * Returns the ID of the currently logged-in user.
     *
     * @return the user ID, or -1 if no user is logged in
     */
    public static int getUserId() {
        return loggedInUserId;
    }

    /**
     * Sets the currently logged-in user's ID.
     *
     * @param userId the user ID to set as logged in
     */
    public static void setUserId(int userId) {
        loggedInUserId = userId;
    }

    /**
     * Checks whether a user is currently logged in.
     *
     * @return {@code true} if a user is logged in; {@code false} otherwise
     */
    public static boolean isLoggedIn() {
        return loggedInUserId != -1;
    }

    /**
     * Logs out the current user by clearing the session.
     */
    public static void logout() {
        loggedInUserId = -1;
    }
}
