package com.iats.app.repository;

import com.iats.app.db.DatabaseConnection;
import com.iats.app.model.Candidate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class CandidateRepository {
    public int save(Candidate candidate) throws SQLException {
        String sql = "INSERT INTO Candidates (full_name, email, phone, resume_url, resume_text) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, candidate.getFullName());
            statement.setString(2, candidate.getEmail());
            statement.setString(3, candidate.getPhone());
            statement.setString(4, candidate.getResumeUrl());
            statement.setString(5, candidate.getResumeText());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    candidate.setCandidateId(id);
                    return id;
                }
            }
            throw new SQLException("Failed to create candidate");
        }
    }

    public Optional<Candidate> findById(int candidateId) throws SQLException {
        String sql = "SELECT candidate_id, full_name, email, phone, resume_url, resume_text FROM Candidates WHERE candidate_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, candidateId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Candidate candidate = new Candidate(
                            resultSet.getString("full_name"),
                            resultSet.getString("email"),
                            resultSet.getString("phone"),
                            resultSet.getString("resume_url"),
                            resultSet.getString("resume_text")
                    );
                    candidate.setCandidateId(resultSet.getInt("candidate_id"));
                    return Optional.of(candidate);
                }
                return Optional.empty();
            }
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Candidates";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        }
    }
}
