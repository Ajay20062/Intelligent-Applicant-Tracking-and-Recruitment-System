package com.iats.app.repository;

import com.iats.app.db.DatabaseConnection;
import com.iats.app.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UserRepository {
    public int save(User user) throws SQLException {
        String sql = "INSERT INTO users (full_name, email, username, role, password_hash) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getFullName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getRole());
            statement.setString(5, user.getPasswordHash());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    user.setUserId(id);
                    return id;
                }
            }
            throw new SQLException("Failed to create user.");
        }
    }

    public Optional<User> findByEmailOrUsername(String loginId) throws SQLException {
        String sql = "SELECT user_id, full_name, email, username, role, password_hash FROM users WHERE email = ? OR username = ? LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, loginId);
            statement.setString(2, loginId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User(
                            resultSet.getString("full_name"),
                            resultSet.getString("email"),
                            resultSet.getString("username"),
                            resultSet.getString("role"),
                            resultSet.getString("password_hash")
                    );
                    user.setUserId(resultSet.getInt("user_id"));
                    return Optional.of(user);
                }
                return Optional.empty();
            }
        }
    }
}
