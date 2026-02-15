package com.iats.app.repository;

import com.iats.app.db.DatabaseConnection;
import com.iats.app.model.Job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class JobRepository {
    public int save(Job job) throws SQLException {
        String sql = "INSERT INTO Jobs (recruiter_id, title, department, location, required_skills, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, job.getRecruiterId());
            statement.setString(2, job.getTitle());
            statement.setString(3, job.getDepartment());
            statement.setString(4, job.getLocation());
            statement.setString(5, job.getRequiredSkills());
            statement.setString(6, job.getStatus());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    job.setJobId(id);
                    return id;
                }
            }
            throw new SQLException("Failed to create job");
        }
    }

    public Optional<Job> findById(int jobId) throws SQLException {
        String sql = "SELECT job_id, recruiter_id, title, department, location, required_skills, status FROM Jobs WHERE job_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, jobId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Job job = new Job(
                            resultSet.getInt("recruiter_id"),
                            resultSet.getString("title"),
                            resultSet.getString("department"),
                            resultSet.getString("location"),
                            resultSet.getString("required_skills"),
                            resultSet.getString("status")
                    );
                    job.setJobId(resultSet.getInt("job_id"));
                    return Optional.of(job);
                }
                return Optional.empty();
            }
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Jobs";
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
