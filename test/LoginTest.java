package test;

import services.AuthService;

public class LoginTest {

    public static void main(String[] args) {
        AuthService authService = new AuthService();

        System.out.println("Testing registration with new features...");

        // Test registration with organization
        boolean result1 = authService.register("testuser1", "password123", "user", "Tech Corp");
        System.out.println("User registration with organization: " + result1);
        if (!result1) {
            System.out.println("Error: " + authService.getLastError());
        }

        // Test registration as admin without organization
        boolean result2 = authService.register("testadmin1", "admin123", "admin", null);
        System.out.println("Admin registration without organization: " + result2);
        if (!result2) {
            System.out.println("Error: " + authService.getLastError());
        }

        // Test login
        boolean loginResult = authService.login("testuser1", "password123");
        System.out.println("Login test: " + loginResult);
        if (loginResult) {
            System.out.println("Current user: " + authService.getCurrentUser().getUsername());
            System.out.println("Role: " + authService.getCurrentUser().getRole());
            System.out.println("Organization: " + authService.getCurrentUser().getOrganization());
        } else {
            System.out.println("Error: " + authService.getLastError());
        }

        // Test duplicate registration
        boolean result3 = authService.register("testuser1", "newpassword", "user", "Other Corp");
        System.out.println("Duplicate username test: " + result3);
        if (!result3) {
            System.out.println("Expected error: " + authService.getLastError());
        }
    }
}
