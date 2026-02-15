package com.iats.app.service;

import com.iats.app.model.User;
import com.iats.app.repository.UserRepository;
import com.iats.app.security.PasswordUtil;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    public void register(String fullName, String email, String username, String role, String password, String confirmPassword)
            throws SQLException {
        validateRegistration(fullName, email, username, role, password, confirmPassword);

        String passwordHash = PasswordUtil.hash(password);
        User user = new User(fullName.trim(), email.trim(), username.trim(), role.trim(), passwordHash);
        userRepository.save(user);
    }

    public Optional<User> login(String loginId, String password) throws SQLException {
        if (loginId == null || loginId.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Login ID and password are required.");
        }

        Optional<User> userOptional = userRepository.findByEmailOrUsername(loginId.trim());
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();
        String enteredHash = PasswordUtil.hash(password);
        if (enteredHash.equals(user.getPasswordHash())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    private void validateRegistration(String fullName, String email, String username, String role, String password, String confirmPassword) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Valid email is required.");
        }
        if (username == null || username.isBlank() || username.length() < 4) {
            throw new IllegalArgumentException("Username must be at least 4 characters.");
        }
        if (role == null || (!role.equals("Admin") && !role.equals("Recruiter"))) {
            throw new IllegalArgumentException("Role must be Admin or Recruiter.");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
    }
}
