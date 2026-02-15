package com.iats.app.repository;

import com.iats.app.db.DatabaseConnection;
import com.iats.app.model.Recruiter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RecruiterRepository {
    public int save(Recruiter recruiter) throws SQLException {
        String sql = "INSERT INTO Recruiters (full_name, email, company) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, recruiter.getFullName());
            statement.setString(2, recruiter.getEmail());
            statement.setString(3, recruiter.getCompany());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    recruiter.setRecruiterId(id);
                    return id;
                }
            }
            throw new SQLException("Failed to create recruiter");
        }
    }

    public List<Recruiter> findAll() throws SQLException {
        String sql = "SELECT recruiter_id, full_name, email, company FROM Recruiters ORDER BY recruiter_id";
        List<Recruiter> recruiters = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Recruiter recruiter = new Recruiter(
                        resultSet.getString("full_name"),
                        resultSet.getString("email"),
                        resultSet.getString("company")
                );
                recruiter.setRecruiterId(resultSet.getInt("recruiter_id"));
                recruiters.add(recruiter);
            }
        }
        return recruiters;
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Recruiters";
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
