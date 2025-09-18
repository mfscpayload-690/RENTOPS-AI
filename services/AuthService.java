package services;

import dao.UserDAO;
import models.User;

public class AuthService {

    private UserDAO userDAO = new UserDAO();
    private User currentUser;
    private String lastError = "";

    public boolean login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            lastError = "Username cannot be empty";
            return false;
        }
        if (password == null || password.isEmpty()) {
            lastError = "Password cannot be empty";
            return false;
        }

        User user = userDAO.login(username.trim(), password);
        if (user != null) {
            currentUser = user;
            lastError = "";
            return true;
        }
        lastError = userDAO.getLastError();
        return false;
    }

    public boolean register(String username, String password, String role) {
        return register(username, password, role, null);
    }

    public boolean register(String username, String password, String role, String organization) {
        // Input validation
        if (username == null || username.trim().isEmpty()) {
            lastError = "Username cannot be empty";
            return false;
        }
        if (password == null || password.length() < 4) {
            lastError = "Password must be at least 4 characters long";
            return false;
        }
        if (username.trim().length() < 3) {
            lastError = "Username must be at least 3 characters long";
            return false;
        }
        if (username.trim().length() > 50) {
            lastError = "Username cannot be longer than 50 characters";
            return false;
        }

        boolean result = userDAO.register(username.trim(), password, role, organization);
        if (result) {
            lastError = "";
        } else {
            lastError = userDAO.getLastError();
        }
        return result;
    }

    public String getLastError() {
        return lastError;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
        lastError = "";
    }

    public boolean isAdmin() {
        return currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    public boolean isUser() {
        return currentUser != null && "user".equalsIgnoreCase(currentUser.getRole());
    }
}
