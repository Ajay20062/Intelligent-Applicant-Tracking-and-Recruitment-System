package com.iats.app.repository;

import com.iats.app.db.DatabaseConnection;
import com.iats.app.model.Interview;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class InterviewRepository {
    public int save(Interview interview) throws SQLException {
        String sql = "INSERT INTO Interviews (application_id, scheduled_at, interview_type, status) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, interview.getApplicationId());
            statement.setTimestamp(2, Timestamp.valueOf(interview.getScheduledAt()));
            statement.setString(3, interview.getInterviewType());
            statement.setString(4, interview.getStatus());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    interview.setInterviewId(id);
                    return id;
                }
            }
            throw new SQLException("Failed to create interview.");
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Interviews";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        }
    }

    public boolean hasConflict(LocalDateTime scheduledAt) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM Interviews
                WHERE status = 'Scheduled'
                  AND ABS(TIMESTAMPDIFF(MINUTE, scheduled_at, ?)) < 60
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(scheduledAt));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
                return false;
            }
        }
    }
}
