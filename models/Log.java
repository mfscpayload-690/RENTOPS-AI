package models;

import java.time.LocalDateTime;

public class Log {
    private int id;
    private String action;
    private int userId;
    private LocalDateTime timestamp;

    public Log(int id, String action, int userId, LocalDateTime timestamp) {
        this.id = id;
        this.action = action;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
