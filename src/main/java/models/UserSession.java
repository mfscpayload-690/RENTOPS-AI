package models;

import java.time.LocalDateTime;

public class UserSession {

    private int id;
    private int userId;
    private String sessionToken;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessed;
    private boolean isActive;

    public UserSession(int id, int userId, String sessionToken, LocalDateTime createdAt, LocalDateTime lastAccessed, boolean isActive) {
        this.id = id;
        this.userId = userId;
        this.sessionToken = sessionToken;
        this.createdAt = createdAt;
        this.lastAccessed = lastAccessed;
        this.isActive = isActive;
    }

    // Constructor for creating new sessions
    public UserSession(int userId, String sessionToken) {
        this.userId = userId;
        this.sessionToken = sessionToken;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.lastAccessed = LocalDateTime.now();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(LocalDateTime lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
