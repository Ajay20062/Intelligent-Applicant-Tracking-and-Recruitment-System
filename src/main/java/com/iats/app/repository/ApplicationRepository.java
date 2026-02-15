package com.iats.app.repository;

import com.iats.app.db.DatabaseConnection;
import com.iats.app.model.ApplicationReportRow;
import com.iats.app.model.Application;
import com.iats.app.model.CandidateRankingRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApplicationRepository {
    public int save(Application application) throws SQLException {
        String sql = "INSERT INTO Applications (job_id, candidate_id, status) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, application.getJobId());
            statement.setInt(2, application.getCandidateId());
            statement.setString(3, application.getStatus());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    application.setApplicationId(id);
                    return id;
                }
            }
            throw new SQLException("Failed to create application");
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Applications";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        }
    }

    public Optional<Application> findById(int applicationId) throws SQLException {
        String sql = "SELECT application_id, job_id, candidate_id, status FROM Applications WHERE application_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, applicationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Application application = new Application(
                            resultSet.getInt("job_id"),
                            resultSet.getInt("candidate_id"),
                            resultSet.getString("status")
                    );
                    application.setApplicationId(resultSet.getInt("application_id"));
                    return Optional.of(application);
                }
                return Optional.empty();
            }
        }
    }

    public boolean updateStatus(int applicationId, String status) throws SQLException {
        String sql = "UPDATE Applications SET status = ? WHERE application_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, applicationId);
            return statement.executeUpdate() > 0;
        }
    }

    public List<Application> findAll() throws SQLException {
        String sql = "SELECT application_id, job_id, candidate_id, status FROM Applications ORDER BY application_id DESC";
        List<Application> applications = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Application application = new Application(
                        resultSet.getInt("job_id"),
                        resultSet.getInt("candidate_id"),
                        resultSet.getString("status")
                );
                application.setApplicationId(resultSet.getInt("application_id"));
                applications.add(application);
            }
        }
        return applications;
    }

    public List<ApplicationReportRow> getApplicationReportRows() throws SQLException {
        String sql = """
                SELECT a.application_id,
                       c.full_name AS candidate_name,
                       j.title AS job_title,
                       a.status,
                       COALESCE(s.score, 0) AS score,
                       COALESCE(s.recommendation, 'Not Screened') AS recommendation
                FROM Applications a
                JOIN Candidates c ON a.candidate_id = c.candidate_id
                JOIN Jobs j ON a.job_id = j.job_id
                LEFT JOIN ApplicationScreenings s ON a.application_id = s.application_id
                ORDER BY a.application_id DESC
                """;
        List<ApplicationReportRow> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                rows.add(new ApplicationReportRow(
                        resultSet.getInt("application_id"),
                        resultSet.getString("candidate_name"),
                        resultSet.getString("job_title"),
                        resultSet.getString("status"),
                        resultSet.getDouble("score"),
                        resultSet.getString("recommendation")
                ));
            }
        }
        return rows;
    }

    public List<CandidateRankingRow> findTopCandidatesForJob(int jobId, int limit) throws SQLException {
        String sql = """
                SELECT a.application_id,
                       c.candidate_id,
                       c.full_name AS candidate_name,
                       COALESCE(s.score, 0) AS score,
                       COALESCE(s.recommendation, 'Not Screened') AS recommendation,
                       COALESCE(s.matched_skills, '') AS matched_skills,
                       COALESCE(s.missing_skills, '') AS missing_skills
                FROM Applications a
                JOIN Candidates c ON a.candidate_id = c.candidate_id
                LEFT JOIN ApplicationScreenings s ON a.application_id = s.application_id
                WHERE a.job_id = ?
                ORDER BY score DESC, a.application_id ASC
                LIMIT ?
                """;
        List<CandidateRankingRow> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, jobId);
            statement.setInt(2, limit);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(new CandidateRankingRow(
                            resultSet.getInt("application_id"),
                            resultSet.getInt("candidate_id"),
                            resultSet.getString("candidate_name"),
                            resultSet.getDouble("score"),
                            resultSet.getString("recommendation"),
                            resultSet.getString("matched_skills"),
                            resultSet.getString("missing_skills")
                    ));
                }
            }
        }
        return rows;
    }
}
