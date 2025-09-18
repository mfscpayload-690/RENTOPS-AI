package test;

import dao.SessionDAO;
import dao.UserDAO;
import models.User;
import services.AuthService;

public class SessionTestImproved {

    public static void main(String[] args) {
        System.out.println("Testing improved session persistence...");

        // Clean up all existing sessions first
        System.out.println("\n0. Cleaning up existing sessions...");
        SessionDAO sessionDAO = new SessionDAO();
        sessionDAO.cleanupExpiredSessions(); // This should clean all sessions

        // Manual cleanup - deactivate all sessions for test user
        UserDAO userDAO = new UserDAO();
        User testUser = userDAO.login("testuser1", "password123"); // This will get the user
        if (testUser != null) {
            sessionDAO.deactivateUserSessions(testUser.getId());
            System.out.println("Deactivated existing sessions for user: " + testUser.getUsername());
        }

        // Test 1: Login and create session
        System.out.println("\n1. Testing login and session creation...");
        AuthService authService1 = new AuthService();
        boolean loginResult = authService1.login("testuser1", "password123");
        System.out.println("Login result: " + loginResult);

        if (loginResult) {
            System.out.println("Current user: " + authService1.getCurrentUser().getUsername());
            System.out.println("Is logged in: " + authService1.isLoggedIn());
        }

        // Test 2: Restore session from another AuthService instance
        System.out.println("\n2. Testing session restoration...");
        AuthService authService2 = new AuthService();
        boolean restoreResult = authService2.restoreSession();
        System.out.println("Session restore result: " + restoreResult);

        if (restoreResult) {
            System.out.println("Restored user: " + authService2.getCurrentUser().getUsername());
            System.out.println("Role: " + authService2.getCurrentUser().getRole());
            System.out.println("Organization: " + authService2.getCurrentUser().getOrganization());
        }

        // Test 3: Logout and verify session is cleared
        System.out.println("\n3. Testing logout...");
        boolean logoutResult = authService2.logout();
        System.out.println("Logout result: " + logoutResult);
        System.out.println("Is logged in after logout: " + authService2.isLoggedIn());

        // Test 4: Try to restore session after logout (should fail now)
        System.out.println("\n4. Testing session restoration after logout...");
        AuthService authService3 = new AuthService();
        boolean restoreAfterLogout = authService3.restoreSession();
        System.out.println("Session restore after logout: " + restoreAfterLogout);

        if (restoreAfterLogout) {
            System.out.println("WARNING: Session was restored when it shouldn't be!");
            System.out.println("Restored user: " + authService3.getCurrentUser().getUsername());
        } else {
            System.out.println("CORRECT: No session restored after logout");
        }

        // Test 5: Verify database state
        System.out.println("\n5. Checking database state...");
        if (testUser != null) {
            // Check if there are any active sessions for our test user
            models.UserSession activeSession = sessionDAO.getActiveSession();
            if (activeSession != null) {
                System.out.println("Found active session for user ID: " + activeSession.getUserId());
                if (activeSession.getUserId() == testUser.getId()) {
                    System.out.println("WARNING: Active session still exists for test user!");
                } else {
                    System.out.println("Active session belongs to different user - this is why restoration worked");
                }
            } else {
                System.out.println("CORRECT: No active sessions found");
            }
        }

        System.out.println("\nImproved session test completed!");
    }
}
