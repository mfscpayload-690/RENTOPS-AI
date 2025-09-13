package services;

import dao.UserDAO;
import models.User;

public class AuthService {
    private UserDAO userDAO = new UserDAO();
    private User currentUser;

    public boolean login(String username, String password) {
        User user = userDAO.login(username, password);
        if (user != null) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public boolean register(String username, String password, String role) {
        return userDAO.register(username, password, role);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isAdmin() {
        return currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    public boolean isUser() {
        return currentUser != null && "user".equalsIgnoreCase(currentUser.getRole());
    }
}
