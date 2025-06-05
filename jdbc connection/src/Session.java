public class Session {
    private static int currentUserId;
    private static String currentUsername;

    // Set the current user details (userId and username)
    public static void setCurrentUser(int userId, String username) {
        currentUserId = userId;
        currentUsername = username;
    }

    // Set the current username (for consistency)
    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    // Get the current user ID
    public static int getCurrentUserId() {
        return currentUserId;
    }

    // Get the current username
    public static String getCurrentUsername() {
        return currentUsername;
    }
}
