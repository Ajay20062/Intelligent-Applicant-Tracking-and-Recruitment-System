package com.iats.app.repository;

import com.iats.app.db.DatabaseConnection;
import com.iats.app.model.ApplicationScreening;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class ApplicationScreeningRepository {
    public void upsert(ApplicationScreening screening) throws SQLException {
        String sql = """
                INSERT INTO ApplicationScreenings (application_id, score, matched_skills, missing_skills, recommendation)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    score = VALUES(score),
                    matched_skills = VALUES(matched_skills),
                    missing_skills = VALUES(missing_skills),
                    recommendation = VALUES(recommendation),
                    screened_at = CURRENT_TIMESTAMP
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, screening.getApplicationId());
            statement.setDouble(2, screening.getScore());
            statement.setString(3, screening.getMatchedSkills());
            statement.setString(4, screening.getMissingSkills());
            statement.setString(5, screening.getRecommendation());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    screening.setScreeningId(keys.getInt(1));
                }
            }
        }
    }

    public Optional<ApplicationScreening> findByApplicationId(int applicationId) throws SQLException {
        String sql = "SELECT screening_id, application_id, score, matched_skills, missing_skills, recommendation FROM ApplicationScreenings WHERE application_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, applicationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    ApplicationScreening screening = new ApplicationScreening(
                            resultSet.getInt("application_id"),
                            resultSet.getDouble("score"),
                            resultSet.getString("matched_skills"),
                            resultSet.getString("missing_skills"),
                            resultSet.getString("recommendation")
                    );
                    screening.setScreeningId(resultSet.getInt("screening_id"));
                    return Optional.of(screening);
                }
                return Optional.empty();
            }
        }
    }
}
