package services;

import dao.SessionDAO;
import dao.UserDAO;
import models.User;
import models.UserSession;

public class SessionManager {

    private SessionDAO sessionDAO;
    private UserDAO userDAO;
    private String currentSessionToken;
    private User currentUser;

    public SessionManager() {
        this.sessionDAO = new SessionDAO();
        this.userDAO = new UserDAO();
    }

    /**
     * Creates a new session for the user
     */
    public boolean createSession(User user) {
        if (user == null) {
            return false;
        }

        String sessionToken = sessionDAO.createSession(user.getId());
        if (sessionToken != null) {
            this.currentSessionToken = sessionToken;
            this.currentUser = user;
            return true;
        }
        return false;
    }

    /**
     * Checks if there's an active session and restores it
     */
    public boolean restoreSession() {
        UserSession activeSession = sessionDAO.getActiveSession();
        if (activeSession != null) {
            // Get user details for this session
            User user = userDAO.getById(activeSession.getUserId());
            if (user != null) {
                this.currentSessionToken = activeSession.getSessionToken();
                this.currentUser = user;

                // Update last accessed time
                sessionDAO.updateSessionAccess(currentSessionToken);
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the current session's last accessed time
     */
    public void updateSessionActivity() {
        if (currentSessionToken != null) {
            sessionDAO.updateSessionAccess(currentSessionToken);
        }
    }

    /**
     * Logs out the current user by deactivating the session
     */
    public boolean logout() {
        if (currentSessionToken != null) {
            boolean success = sessionDAO.deactivateSession(currentSessionToken);
            if (success) {
                this.currentSessionToken = null;
                this.currentUser = null;
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if user is currently logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null && currentSessionToken != null;
    }

    /**
     * Gets the current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Gets the current session token
     */
    public String getCurrentSessionToken() {
        return currentSessionToken;
    }

    /**
     * Checks if current user is admin
     */
    public boolean isAdmin() {
        return currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Checks if current user is regular user
     */
    public boolean isUser() {
        return currentUser != null && "user".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Cleans up expired sessions (maintenance operation)
     */
    public void cleanupExpiredSessions() {
        sessionDAO.cleanupExpiredSessions();
    }
}
