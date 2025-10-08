package com.rentops.ai.logging;

import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DAO for persisting AI query logs.
 */
public class AiQueryLogDao {

    private static final String INSERT_SQL = "INSERT INTO ai_query_log (task, model, prompt_hash, prompt_chars, response_chars, latency_ms, success, error_type, created_at) VALUES (?,?,?,?,?,?,?,?,?)";

    public void insert(AiQueryLog log) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setString(1, log.task());
            ps.setString(2, log.model());
            ps.setString(3, log.promptHash());
            ps.setInt(4, log.promptChars());
            ps.setInt(5, log.responseChars());
            ps.setLong(6, log.latencyMs());
            ps.setBoolean(7, log.success());
            ps.setString(8, log.errorType());
            ps.setTimestamp(9, java.sql.Timestamp.from(log.createdAt()));
            ps.executeUpdate();
        }
    }
}
