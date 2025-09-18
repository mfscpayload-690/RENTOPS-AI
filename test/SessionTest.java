package test;

import services.AuthService;

public class SessionTest {

    public static void main(String[] args) {
        System.out.println("Testing session persistence...");

        AuthService authService = new AuthService();

        // Test 1: Login and create session
        System.out.println("\n1. Testing login and session creation...");
        boolean loginResult = authService.login("testuser1", "password123");
        System.out.println("Login result: " + loginResult);

        if (loginResult) {
            System.out.println("Current user: " + authService.getCurrentUser().getUsername());
            System.out.println("Is logged in: " + authService.isLoggedIn());
        }

        // Test 2: Create new AuthService instance and restore session
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

        // Test 4: Try to restore session after logout
        System.out.println("\n4. Testing session restoration after logout...");
        AuthService authService3 = new AuthService();
        boolean restoreAfterLogout = authService3.restoreSession();
        System.out.println("Session restore after logout: " + restoreAfterLogout);

        System.out.println("\nSession test completed!");
    }
}
